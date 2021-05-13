# Payment System
+ 결제요청을 받아 카드사와 통신하는 인터페이스를 제공하는 결제시스템


### 1. 목적
- 결제요청, 결제취소, 결제 정보 조회 API를 구현하세요.<br/><br/>
- 결제요청, 결제취소 건의 경우 카드사로 데이터 전송을 가정하여 Embedded h2에 저장하세요.<br/><br/>
- 카드정보 암/복호화 기능 개발하세요.<br/><br/>
- 트랜잭션 데이터 관리가 가능하도록 개발하세요.<br/><br/>

### 2. 개발 환경

- Spring Tool Suite 3 : Version: 3.9.15.RELEASE<br/><br/>
- OS : Windows 10

### 3. 개발 프레임워크 구성

- 개발 프레임워크 구성은 아래와 같으며, Gradle 스크립트에 의해서 자동으로 로드 및 사용하도록 되어있음<br/><br/>

+ Apache<br/>
    + 개발 편의를 위해 사용<br/>

    + apache-commons-lang3<br/>

+ Json<br/>
    + Json 스트링을  객체화할 때 사용 <br/>

    + jackson-databind<br/>

+ Database<br/>
    + h2database<br/>

+ Spring Boot<br/>
    + Restful 서버 구성, DB 엔티티 관리 및 Valid에 사용<br/>

    + spring-boot-starter-test<br/>
    + spring-boot-starter-data-jpa<br/>
    + spring-boot-starter-jdbc<br/>
    + spring-boot-starter-validation<br/>
    + spring-boot-starter-web<br/>

+ 기타<br/>
    + lombok<br/>
    + Annotation Processor를 활용한 자동 Getter, Setter 기능 및 Builder 생성 시에 사용.<br/>

    + junit<br/>
    + test case를 위해 사용<br/>


### 4. 빌드 및 실행 방법

### 터미널 환경

+ Git, Java 는 설치되어 있다고 가정

``` bash
$ git clone https://github.com/doowonJeon/payment.git
$ cd payment/
$ ./gradlew clean build
$ java -jar build/libs/Payment-0.0.1-SNAPSHOT.jar
```
+ 접속 Base URI: http://localhost:8080

   
### 5. 테이블 구성 

+ Payment<br/>

    + 결제 관리 테이블<br/>

    + Column><br/>

        + String id : PK<br/>
        + int installment_months : 유효기간<br/>
        + Long price : 결제금액(TYPE이 PAYMENT인 경우), 취소금액(TYPE이 CANCEL인 경우)<br/>
        + Long vat : 부가가치세<br/>
        + String card_info : 카드번호, 유효기간, cvc의 encrypted된 값<br/>
        + Boolean cancel_flag : true(CANCEL인 경우) / false(PAYMENT인 경우)<br/>
        + Date created_date : 최초 생성된 일자<br/>

    + cancel_flag(취소 상태) :<br/>
         + true, //CANCEL인 상태<br/>
         + false //PAYMENT인 상태<br/>

+ Company<br/>

    + 카드회사 데이터 테이블<br/>

    + Column><br/>

        + String id : PK<br/>
        + String data : 통신에 사용되는 450자리 string 데이터<br/>



### 6. 문제 해결 전략

#### 서비스 실행 순서

+ 결제 요청<br/><br/>
    + 유효성 검사<br/><br/>
        + 카드정보(카드번호, 유효기간, cvc), 할부개월수, 결제금액, 부가가치세(옵션) 입력정보에 대한 유효성 검사를 한다.<br/><br/>
    + 카드정보 암호화<br/><br/>
    + 카드사 전달용 data 생성<br/><br/>
    + Payment, Company 테이블 저장<br/><br/> 
    + 결과값 리턴<br/><br/>
+ 결제 전체 취소 요청<br/><br/>
    + 유효성 검사<br/><br/>
        + 관리번호, 취소금액, 부가가치세(옵션)에 대한 유효성 검사를 한다.<br/><br/>
    + 관리번호로 Payment 테이블 조회 <br/><br/>
    + 금액, 부가가치세 유효성 검사<br/><br/>
    + 카드정보 복호화<br/><br/>   		
    + 카드사 전달용 data 생성<br/><br/>
    + Payment, Company 테이블 저장<br/><br/>
    + 결과값 리턴<br/><br/>	
+ 결제 부분 취소 요청<br/><br/>
    + 유효성 검사<br/><br/>
        + 관리번호, 취소금액, 부가가치세(옵션)에 대한 유효성 검사를 한다.<br/><br/>
    + 관리번호로 Payment 테이블 조회 <br/><br/>
    + 금액, 부가가치세 유효성 검사<br/><br/>
    + 카드정보 복호화<br/><br/>   		
    + 카드사 전달용 data 생성<br/><br/>
    + Payment, Company 테이블 저장<br/><br/>
    + 결과값 리턴<br/><br/>	
+ 데이터 조회<br/><br/>
    + 유효성 검사<br/><br/>
        + 관리번호에 대한 유효성 검사를 한다.<br/><br/>
    + 관리번호로 Payment 테이블 조회 <br/><br/>
    + 카드정보 복호화<br/><br/>
    + 결과값 리턴<br/><br/>
   
### 7. 서비스 구성

#### controller<br/>

+ REST API를 통해 컨트롤러 진입<br/><br/>

+ PayController<br/><br/>
+ 결제 API : /payment<br/>
  
```java
@PostMapping(value = "/payment")
	public Object payment(@RequestBody @Valid PaymentRequest paymentDto, Errors errors) throws ApiException {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}
		PaymentResponse paymentResponse = paymentService.registerPayment(paymentDto);
		if (paymentResponse == null) {
			return ResponseEntity.badRequest().build();
		}
		return paymentResponse;
	}
```

<br/>

+ 전체 취소 API : /cancel/all<br/>
  
```java
	@PostMapping(value = "/cancel/all")
	public Object cancelAll(@RequestBody @Valid CancelPaymentRequest cancelPaymentDto, BindingResult bindingResult)
			throws ApiException {
		if (bindingResult.hasErrors()) {
			throw new ApiParameterException(bindingResult);
		} else {
			CancelPaymentResponse cancelPaymentResponse = cancelAllpaymentService.cancelAllPayment(cancelPaymentDto);
			return cancelPaymentResponse;
		}
	}
```

<br/>

+ 취소 API : /cancel<br/>
  
```java
	@PostMapping(value = "/cancel")
	public Object cancel(@RequestBody @Valid CancelPaymentRequest cancelPaymentDto, BindingResult bindingResult)
			throws ApiException {
		if (bindingResult.hasErrors()) {
			throw new ApiParameterException(bindingResult);
		} else {
			CancelPaymentResponse cancelPaymentResponse = cancelpaymentService.cancelPayment(cancelPaymentDto);
			return cancelPaymentResponse;
		}
	}
```

<br/>

+ 조회 API : /payment/{id}<br/>
  
```java
	@GetMapping(value = "/payment/{id}")
	public Object select(@Valid SelectPaymentRequest selectPaymentDto, Errors errors) throws ApiException {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}
		SelectPaymentResponse selectPaymentResponse = selectPaymentService.selectPayment(selectPaymentDto);
		if (selectPaymentResponse == null) {
			return ResponseEntity.badRequest().build();
		}
		return selectPaymentResponse;
	}
```

#### request<br/>

+ 입력 파라미터에 대한 유효성 검사<br/><br/>

+ PaymentRequest<br/><br/>
  
```java
@Data
public class PaymentRequest {
	@NotNull(message = "카드정보는 필수값 입니다.")
	@Valid
	private CardRequest card_info;

	@Min(value = 0, message = "일시불의 경우 0, 할부의 경우 최소 1을 입력해주세요.")
	@Max(value = 12, message = "할부는 최대 12개월까지 가능합니다.")
	@NotNull(message = "할부개월수는 필수값 입니다.")
	private Integer installmentMonths;

	@Min(value = 100, message = "최소결재금액은 100원 이상이여야 합니다.")
	@Max(value = 1000000000, message = "최대 결재금액은 10억원을 초과할 수 없습니다.")
	@NotNull(message = "결제금액은 필수값 입니다.")
	private Long price;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long vat;
```

<br/>

#### service<br/>

+ 실직적인 비즈니스 로직 처리<br/><br/>
    + 주요 처리로직<br/><br/>
        + 카드정보와 관련된 encrypt / decrypt 실행<br/>
        + 금액 및 부가가치세에 대한 유효성 검증<br/>
        + 카드사와의 통신을 위한 데이터 생성<br/>

+ encrypt / decrypt 실행<br/><br/>
    + encrypt 절차<br/>
        + 카드정보의 암호화를 위하여 application.yml에 설정한 SECRET_KEY를 받아옴<br/>
        + plain SECRET_KEY로 PBEKeySpec 이용해서 패스워드기반 키를 생성<br/>
        + 최종 AES 암호 알고리즘으로 SecretKeySpec 클래스를 사용 비밀키 생성<br/>
        + 위의 비밀키로 encrypt(data + key)<br/><br/>
        
    + decrypt 절차<br/>
        + 카드정보의 복호화를 위하여 application.yml에 설정한 SECRET_KEY를 받아옴<br/>
        + plain SECRET_KEY로 PBEKeySpec 이용해서 패스워드기반 키를 생성<br/>
        + 최종 AES 암호 알고리즘으로 SecretKeySpec 클래스를 사용 비밀키 생성<br/>
        + 위의 비밀키로 decrypt(data + key)<br/><br/>

    + AESUtil<br/><br/>
  
```java
public class AESUtil {

	private static final byte[] IV = "kakaoaesutil2021".getBytes(); // 첫 블럭과 XOR 연산을 해야되기 때문에 iv의 길이는 블럭 사이즈인 16 byte
	private static final String ALGO = "AES/CBC/PKCS5Padding";
	private static final String SALT = "-ZHkk{,*jbU78/?s7Ew7l@BcG:L]OosMo:i/%ut@=cydO9No2z)sP@&@Xa137e";

	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(n);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}

	public static SecretKey getKeyFromPassword(String SECRET_KEY) throws ApiException {

		SecretKeyFactory factory;
		SecretKey secret = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
			secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		} catch (Exception e) {
			throw new ApiException(ErrorCode.GET_KEY_FROM_PASSWORD, e.getLocalizedMessage());
		}
		return secret;
	}

	public static String encrypt(String input, String SECRET_KEY) throws ApiException {

		SecretKey key = getKeyFromPassword(SECRET_KEY);
		byte[] cipherText = null;

		try {
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
			cipherText = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			throw new ApiException(ErrorCode.ENCRYPT, e.getLocalizedMessage());
		}
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decrypt(String cipherText, String SECRET_KEY) throws ApiException {

		SecretKey key = getKeyFromPassword(SECRET_KEY);
		byte[] plainText = null;

		try {
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
			plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		} catch (Exception e) {
			throw new ApiException(ErrorCode.DECRYPT, e.getLocalizedMessage());
		}
		return new String(plainText);
	}
}

```

<br/>

+ 유효성 검증<br/><br/>
    + 취소 시 금액 및 부가가치세에 대한 처리 로직<br/>
        
```java
	private ValidResult checkValid(CancelPaymentRequest cancelPaymentDto, Payment payment) throws ApiException {
		long currentVat = payment.getVat();
		long currentPrice = payment.getPrice();

		long requestPrice = cancelPaymentDto.getPrice();
		long requestVat;

		boolean vat_null_flag = false;

		if (cancelPaymentDto.getVat() == null) {
			requestVat = BigDecimal.valueOf(requestPrice).divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP)
					.longValue();
			vat_null_flag = true;
		} else {
			requestVat = cancelPaymentDto.getVat();
		}

		if (requestPrice > currentPrice) {
			throw new ApiException(ErrorCode.SHORT_PRICE);
		}

		if (requestVat > currentVat) {
			if (!vat_null_flag) {
				throw new ApiException(ErrorCode.SHORT_VAT);
			} else {
				if (requestVat > currentVat) {
					requestVat = currentVat;
				}
			}
		}

		if (requestPrice < requestVat) {
			throw new ApiException(ErrorCode.VAT_GREATER_THAN_PRICE);
		}

		if (currentPrice - requestPrice < currentVat - requestVat) {
			throw new ApiException(ErrorCode.VAT_GREATER_THAN_PRICE);
		}

		return new ValidResult(currentPrice - requestPrice, currentVat - requestVat);
	}

```

+ 데이터 생성<br/><br/>
    + 카드사와의 통신을 위한 데이터 생성로직<br/>
    + StringUtils의 pad를 사용하여 구현<br/>
        
```java
public static String setString(Payment payment, String type, String cardInfo) {
		String[] card_info_arr = cardInfo.split("_");

		String header_leng = lnumber(446, 4);
		String header_type = rstring(type, 10);
		String header_num = rstring(payment.getId(), 20);

		String data_cardnum = rnumber(card_info_arr[0], 20);
		String data_installment = lnumber_zero(payment.getInstallment_months(), 2);
		String data_validdate = rnumber(card_info_arr[1], 4);
		String data_cvc = rnumber(card_info_arr[2], 3);
		String data_price = lnumber(payment.getPrice(), 10);
		String data_vat = lnumber_zero(payment.getVat(), 10);
		String data_managenum = rstring("", 20);
		if (type == "CANCEL") {
			data_managenum = rstring(payment.getId(), 20);
		}
		String data_encdata = rstring(payment.getCard_info(), 300);
		String data_pause = rstring("", 47);

		String string_data = new StringBuilder(header_leng).append(header_type).append(header_num).append(data_cardnum)
				.append(data_installment).append(data_validdate).append(data_cvc).append(data_price).append(data_vat)
				.append(data_managenum).append(data_encdata).append(data_pause).toString();
		return string_data;

	}

	public static String lnumber(Object input, int length) {
		return StringUtils.leftPad(String.valueOf(input), length, " ");
	}

	public static String lnumber_zero(Object input, int length) {
		return StringUtils.leftPad(String.valueOf(input), length, "0");
	}

	public static String rnumber(Object input, int length) {
		return StringUtils.rightPad(String.valueOf(input), length, " ");
	}

	public static String rstring(Object input, int length) {
		return StringUtils.rightPad(String.valueOf(input), length, " ");
	}

```

### 8. Exception

#### ExceptionController<br/>

+ @ExceptionHandler(ApiException.class) : ApiException 발생시 message 처리<br/><br/>

```java
@RestControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ApiException.class)
	@ResponseBody
	protected ResponseEntity<ErrorResponse> exceptionController(ApiException e) {
		return ResponseEntity.badRequest().body(new ErrorResponse(e.getError(), e.getMessage(), null));
	}
}

```

    
#### ApiParameterException<br/>

+ API 요청 최초 진입시 Parameter에 대한 exception 발생시 message 처리<br/><br/>

```java
public class ApiParameterException extends ApiException {
	@Getter
	private List<ErrorInfo> fields;

	public ApiParameterException(BindingResult result) {
		super(ErrorCode.INVALID_PARAMETER);
		this.fields = result.getAllErrors().stream()
				.map(error -> new ErrorInfo(error.getCode(), error.getObjectName(), error.getDefaultMessage()))
				.collect(Collectors.toList());
	}

	@Data
	public class ErrorInfo {
		private String code;

		private String field;

		private String message;

		ErrorInfo(String code, String field, String message) {
			this.code = code;
			this.field = field;
			this.message = message;
		}
	}
}
```

+ ApiException
    + 전체 비즈니스 로직 내에서 발생하는 error를 처리하는 Exception    


#### 오류코드<br/>
+ 비지니스 로직 내에 오류 발생 시 ExceptionController를 통해 오류 코드 및 내용을 리턴하도록 구성

#### ExceptionController<br/>
+ Message 및 code<br/><br/>

```java
    ERROR("C0001", "error"),
    ENCRYPT("C0002", "ENCRYPT error"),
    DECRYPT("C0003", "DECRYPT error"),
    GET_KEY_FROM_PASSWORD("C0004", "GET_KEY_FROM_PASSWORD error"),
    VAT_WRONG("C0005", "VAT is Wrong"),
    NOT_FOUND("C0006", "Not found Payment"),
    SHORT_PRICE("C0007", "The cancellation price is higher."),
    SHORT_VAT("C0008", "The cancellation vat is higher."),
    VAT_GREATER_THAN_PRICE("C0009", "VAT이 금액보다 큽니다."),
    INVALID_PARAMETER("C0010", "Invalid parameter"),
    INVALID_CANCEL("C0011", "Cancellation is only possible once."),
    INVALID_VAT("C0012","VAT cannot be greater than the amount paid.")

```


### 9. API

#### 카드결제<br/>

+ URL : /api/payment<br/><br/>
+ 요청방식 : POST<br/><br/>
+ input : RequestBody<br/><br/>

```java
{   
    "card_info": {
        "cardNumber": "1234567890",
        "validDate": "0721",
        "cvc": "123"
    },
    "installmentMonths": 12,
    "price": 11000,
    "vat": 1000
}

```

+ Success response<br/><br/>
    + status code : 200 OK

```java

{
    "id": "gZzBjgc49HN7dBfEslbP",
    "data": " 446PAYMENT   gZzBjgc49HN7dBfEslbP1234567890          120721123     110000000001000                    Ls5ZyRrhPx5iXxit4G2fiUTBpYK7qG7VxrjnMdsuYOo=                                                                                                                                                                                                                                                                                                               "
}
```

+ Fail response<br/><br/>
    + 파라미커가 유효하지 않은 경우<br/><br/>
    + status code : 400Bad Request

```java
{
    "description": "Invalid parameter",
    "code": "C0010"
}

```

#### 결제 전체 취소<br/>

+ URL : /api/cancel/all<br/><br/>
+ 요청방식 : POST<br/><br/>
+ input : RequestBody<br/><br/>

```java
{
    "id": "6LE125Nft5opT2qqBubf",
    "price": 11000,
    "vat": ""
}

```

+ Success response<br/><br/>
    + status code : 200 OK

```java
{
    "id": "6LE125Nft5opT2qqBubf",
    "data": " 446CANCEL    6LE125Nft5opT2qqBubf1234567890          000721123     1100000000010006LE125Nft5opT2qqBubfLs5ZyRrhPx5iXxit4G2fiUTBpYK7qG7VxrjnMdsuYOo=                                                                                                                                                                                                                                                                                                               "
}
```

+ Fail response<br/><br/>
    + 파라미커가 유효하지 않은 경우<br/><br/>
    + status code : 400Bad Request

```java
{
    "description": "Invalid parameter",
    "code": "C0010"
}

```

#### 결제 부분 취소<br/>

+ URL : <br/><br/>
+ 요청방식 : POST<br/><br/>
+ input : RequestBody<br/><br/>

```java
{
    "id": "Q5F2YTrZE8gqHGyOgcpm",
    "price": 100,
    "vat": ""
}
```

+ Success response<br/><br/>
    + status code : 200 OK

```java
{
    "id": "Q5F2YTrZE8gqHGyOgcpm",
    "data": " 446CANCEL    Q5F2YTrZE8gqHGyOgcpm32612349874         000721123     109000000000991Q5F2YTrZE8gqHGyOgcpmNijsi78SOw26TCBaygUdUNPBtlmlQ4iCEltbHaeAO4A=                                                                                                                                                                                                                                                                                                               "
}
```

+ Fail response<br/><br/>
    + VATDL PRICE보다 큰 경우<br/><br/>
    + status code : 400Bad Request

```java
{
    "description": "The cancellation vat is higher.",
    "code": "C0008"
}

```

#### 조회<br/>

+ URL : /api/payment/{ID}<br/><br/>
+ 요청방식 : GET<br/><br/>

+ Success response<br/><br/>
    + status code : 200 OK

```java
{
    "id": "Q5F2YTrZE8gqHGyOgcpm",
    "card_info": {
        "cardNumber": "***12349***",
        "validDate": "0721",
        "cvc": "123"
    },
    "type": "CANCEL",
    "price": 10900,
    "vat": 991
}
```

+ Fail response<br/><br/>
    + 파라미커가 유효하지 않은 경우<br/><br/>
    + status code : 400Bad Request

```java
{
    "description": "Invalid parameter",
    "code": "C0010"
}
```


### 10. 단위 테스트

#### TEST 코드
+ PaymentApplicationTests

#### 결제 정보 등록 및 조회<br/>
+ 결제 한건 저장 후 조회 확인 test

```java
	@Test
	public void Test_1() throws Exception {
		System.out.println("[======================[start register and select]======================");
		Payment payment = setParamForRegister();

		payment = paymentRepository.save(payment);

		mockMvc.perform(get("/api/payment/" + payment.getId())).andExpect(status().isOk()).andDo(print());
	}

```

#### 결제 정보 등록 및 취소 후 상태 확인<br/>
+ 결제 한건 저장 후 취소 확인 test
+ 결제 저장 시 상태 값 -  PAYMENT, 취소 후 상태 값 - CANCEL
+ 위의 상태값으로 조회 확인 test

```java
	@Test
	public void Test_2() throws Exception {
		System.out.println("[======================[start register and cancel and Health check]======================");
		Payment payment = setParamForRegister();

		payment = paymentRepository.save(payment);

		MvcResult result = mockMvc.perform(get("/api/payment/" + payment.getId())).andExpect(status().isOk())
				.andDo(print()).andReturn();
		String content = result.getResponse().getContentAsString();
		boolean before_flag = content.contains("PAYMENT");

		CancelPaymentRequest cancelPaymentRequest = CancelPaymentRequest.builder().id(payment.getId()).price(100L)
				.vat(10L).build();

		long requestVat = checkValid(cancelPaymentRequest, payment);
		payment.setVat(requestVat);
		payment.setCancel_flag(true);

		payment = paymentRepository.save(payment);

		result = mockMvc.perform(get("/api/payment/" + payment.getId())).andExpect(status().isOk()).andDo(print())
				.andReturn();
		content = result.getResponse().getContentAsString();
		boolean after_flag = content.contains("CANCEL");

		assertTrue(before_flag == after_flag);

	}
```

#### TEST 코드
+ AESUtilTest

#### encrypt, decrypt 유효성 검증<br/>
+ 순차적으로 encrypt, decrypt 실행 후 plain_text 가 나오는지 검증

```java
	@Test
	public void Test_1() throws Exception {

		String secret_key = "secretsecret";
		String plain_text = "This character should appear";

		String enc_data = AESUtil.encrypt(plain_text, secret_key);

		String dec_data = AESUtil.decrypt(enc_data, secret_key);

		Assertions.assertEquals(plain_text, dec_data);
	}

```
