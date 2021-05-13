package com.pay;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay.controller.PayController;
import com.pay.exception.ApiException;
import com.pay.exception.ErrorCode;
import com.pay.model.Payment;
import com.pay.repository.PaymentRepository;
import com.pay.request.CancelPaymentRequest;
import com.pay.request.CardRequest;
import com.pay.request.PaymentRequest;
import com.pay.util.AESUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@FixMethodOrder(MethodSorters.DEFAULT)
@AutoConfigureMockMvc
class PaymentApplicationTests {

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	public ObjectMapper objectMapper;

	@Value("${secret.key}")
	private String SECRET_KEY;

	@Autowired
	private PaymentRepository paymentRepository;

	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(PayController.class).build();
	}

	/**
	 * 1. 결제 정보 등록 및 조회
	 * 
	 * @throws Exception
	 */
	@Test
	public void Test_1() throws Exception {
		System.out.println("[======================[start register and select]======================");
		Payment payment = setParamForRegister();

		payment = paymentRepository.save(payment);

		mockMvc.perform(get("/api/payment/" + payment.getId())).andExpect(status().isOk()).andDo(print());
	}

	/**
	 * 2. 결제 정보 등록 및 취소 후 상태 확인
	 * 
	 * @throws Exception
	 */
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
		
		result = mockMvc.perform(get("/api/payment/" + payment.getId())).andExpect(status().isOk())
				.andDo(print()).andReturn();
		content = result.getResponse().getContentAsString();
		boolean after_flag = content.contains("CANCEL");

		assertTrue(before_flag == after_flag);

	}

	private Payment setParamForRegister() throws ApiException {
		CardRequest cardRequest = new CardRequest();
		cardRequest.setCardNumber("1234567890");
		cardRequest.setValidDate("0721");
		cardRequest.setCvc("123");

		PaymentRequest paymentDto = PaymentRequest.builder().card_info(cardRequest).installmentMonths(2021)
				.price(10000000L).vat(100L).build();

		String _cardInfo = setCardInfo(paymentDto);
		Payment payment = setPayment(_cardInfo, paymentDto);
		return payment;
	}

	private String setCardInfo(PaymentRequest paymentDto) {
		List<String> list = new ArrayList<String>();
		CardRequest card_info = paymentDto.getCard_info();
		list.add(card_info.getCardNumber());
		list.add(card_info.getValidDate());
		list.add(card_info.getCvc());
		return StringUtils.join(list, "_");
	}

	private Payment setPayment(String _cardInfo, PaymentRequest paymentDto) throws ApiException {
		String enc_card_info = AESUtil.encrypt(_cardInfo, SECRET_KEY);
		Payment payment = new Payment();
		payment.setId(RandomStringUtils.randomAlphanumeric(20));
		payment.setCard_info(enc_card_info);
		payment.setInstallment_months(paymentDto.getInstallmentMonths());
		payment.setPrice(paymentDto.getPrice());
		long vat = 0;
		if (paymentDto.getVat() == null) {
			vat = BigDecimal.valueOf(paymentDto.getPrice()).divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP)
					.longValue();
		} else {
			vat = paymentDto.getVat();
			if (paymentDto.getPrice() < vat) {
				return null;
			}
		}
		payment.setVat(vat);
		return payment;
	}

	private long checkValid(CancelPaymentRequest cancelPaymentDto, Payment payment) throws ApiException {
		long currentPrice = payment.getPrice();
		long currentVat = payment.getVat();

		long requestPrice = cancelPaymentDto.getPrice();
		long requestVat;

		if (cancelPaymentDto.getVat() == null) {
			requestVat = BigDecimal.valueOf(cancelPaymentDto.getPrice())
					.divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP).longValue();
		} else {
			requestVat = cancelPaymentDto.getVat();
		}

		if (requestPrice > currentPrice) {
			throw new ApiException(ErrorCode.SHORT_PRICE);
		}

		if (requestVat > currentVat) {
			throw new ApiException(ErrorCode.SHORT_VAT);
		}

		if (requestPrice < requestVat) {
			throw new ApiException(ErrorCode.VAT_GREATER_THAN_PRICE);
		}

		if (currentPrice - requestPrice < currentVat - requestVat) {
			throw new ApiException(ErrorCode.VAT_GREATER_THAN_PRICE);
		}

		return requestVat;
	}
}
