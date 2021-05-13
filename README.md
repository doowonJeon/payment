# Payment System
+ 결제요청을 받아 카드사와 통신하는 인터페이스를 제공하는 결제시스템


#### 1. 목적
- 결제요청, 결제취소, 결제 정보 조회 API를 구현하세요.<br/><br/>
- 결제요청, 결제취소 건의 경우 카드사로 데이터 전송을 가정하여 Embedded h2에 저장하세요.<br/><br/>
- 카드정보 암/복호화 기능 개발하세요.<br/><br/>
- 트랜잭션 데이터 관리가 가능하도록 개발하세요.<br/><br/>

#### 2. 개발 환경

- Spring Tool Suite 3 : Version: 3.9.15.RELEASE<br/><br/>
- OS : Windows 10

#### 3. 개발 프레임워크 구성

- 개발 프레임워크 구성은 아래와 같으며, Gradle 스크립트에 의해서 자동으로 로드 및 사용하도록 되어있음<br/><br/>

+ Apache<br/><br/>
    + 개발 편의를 위해 사용<br/><br/>

    + apache-commons-lang3<br/><br/>

+ Json<br/><br/>
    + Json 스트링을  객체화할 때 사용 <br/><br/>

    + jackson-databind<br/><br/>

+ Database<br/><br/>
    + h2database<br/><br/>

+ Spring Boot<br/><br/>
    + Restful 서버 구성, DB 엔티티 관리 및 Valid에 사용<br/><br/>

    + spring-boot-starter-test<br/><br/>
    + spring-boot-starter-data-jpa<br/><br/>
    + spring-boot-starter-jdbc<br/><br/>
    + spring-boot-starter-validation<br/><br/>
    + spring-boot-starter-web<br/><br/>

+ 기타<br/><br/>
    + lombok<br/><br/>
    + Annotation Processor를 활용한 자동 Getter, Setter 기능 및 Builder 생성 시에 사용.<br/><br/>

    + junit<br/><br/>
    + test case를 위해 사용<br/><br/>


#### 4. 빌드 및 실행 방법

+ STS에서 아래의 순서로 실행

``` bash
./gradlew bootRun
```


#### 문제 해결 전략

##### 서비스 실행 순서

+ 결제 요청<br/><br/>
    + 유효성 검사<br/><br/>
        + 카드정보(카드번호, 유효기간, cvc), 할부개월수, 결제금액, 부가가치세(옵션) 입력정보에 대한 유효성 검사를 한다.<br/><br/>
    + 카드정보 암호화<br/><br/>
    + 카드사 전달용 data 생성<br/><br/>
    + Payment, Company 테이블 저장<br/><br/> 
    + 결과값 리턴<br/><br/>
+ 결제 취소 요청<br/><br/>
    + 유효성 검사<br/><br/>
        + 관리번호, 취소금액, 부가가치세(옵션)에 대한 유효성 검사를 한다.<br/><br/>
    + 관리번호로 Payment 테이블 조회 <br/><br/>
    + 금액, 부가가치세 유효성 검사
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
   
   
#### 5. 테이블 구성 

+ Payment<br/><br/>

    + 결제 관리 테이블<br/><br/>

    + Column><br/><br/>

        + String id : PK<br/><br/>
        + int installment_months : 유효기간<br/><br/>
        + Long price : 결제금액(TYPE이 PAYMENT인 경우), 취소금액(TYPE이 CANCEL인 경우)<br/><br/>
        + Long vat : 부가가치세<br/><br/>
        + String card_info : 카드번호, 유효기간, cvc의 encrypted된 값<br/><br/>
        + Boolean cancel_flag : true(CANCEL인 경우) / false(PAYMENT인 경우)<br/><br/>
        + Date created_date : 최초 생성된 일자<br/><br/>

    + cancel_flag(취소 상태) :<br/><br/>
         + true, //CANCEL인 상태<br/><br/>
         + false //PAYMENT인 상태<br/><br/>

+ Company<br/><br/>

    + 카드회사 데이터 테이블<br/><br/>

    + Column><br/><br/>

        + String id : PK<br/><br/>
        + String data : 통신에 사용되느 450자리 string 데이터<br/><br/>



#### Controller<br/>
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


+ 취소 API : /cancle<br/><br/>
    
  
```java
@PostMapping(value = "/cancel")
	public Object cancel(@RequestBody @Valid CancelPaymentRequest cancelPaymentDto, Errors errors) throws ApiException {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}
		CancelPaymentResponse cancelPaymentResponse = cancelpaymentService.cancelPayment(cancelPaymentDto);
		return cancelPaymentResponse;
	}
```

+ 조회 API : /payment/{id}<br/><br/>
    
  
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
