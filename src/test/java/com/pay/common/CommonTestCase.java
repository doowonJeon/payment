package com.pay.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay.exception.ErrorCode;
import com.pay.request.CancelPaymentRequest;
import com.pay.request.CardRequest;
import com.pay.request.PaymentRequest;
import com.pay.request.SelectPaymentRequest;
import com.pay.response.CancelPaymentResponse;
import com.pay.response.PaymentResponse;
import com.pay.response.SelectPaymentResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommonTestCase {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	protected CardRequest defaultCardRequest() {
		return new CardRequest("1234567890", "0120", "123");
	}

	protected <T> void assertConstraint(T object, Class<? extends Annotation> clazz) {
		Collection<ConstraintViolation<T>> violations = Validation.buildDefaultValidatorFactory().getValidator()
				.validate(object);

		List<Class> constraints = violations.stream()
				.map(violation -> (ConstraintDescriptorImpl) violation.getConstraintDescriptor())
				.map((Function<ConstraintDescriptorImpl, Class>) ConstraintDescriptorImpl::getAnnotationType)
				.collect(Collectors.toList());

		assert constraints.contains(clazz) : String.format("%s is not constraint contained in [%s]", clazz.getName(),
				StringUtils.join(constraints, ", "));
	}

	protected Object payment(PaymentRequest request, ResultMatcher resultMatcher) throws Throwable {
		try {
			Object response = perform("/api/payment", request, resultMatcher, PaymentResponse.class);

			System.err.println(String.format("Price = %d, vat = %d : 결제 성공", request.getPrice(), request.getVat()));

			return response;
		} catch (Exception e) {
			System.err.println(String.format("Price = %d, vat = %d : 결제 실패 (%s)", request.getPrice(), request.getVat(),
					e.getMessage()));

			throw ExceptionUtils.getRootCause(e);
		}
	}

	protected Object cancel(CancelPaymentRequest request, ResultMatcher resultMatcher) throws Throwable {
		try {
			Object response = perform("/api/cancel", request, resultMatcher, CancelPaymentResponse.class);

			System.err.println(String.format("Price = %d, vat = %d : 취소 성공", request.getPrice(), request.getVat()));

			return response;
		} catch (Exception e) {
			System.err.println(String.format("Price = %d, vat = %d : 취소 실패 (%s)", request.getPrice(), request.getVat(),
					e.getMessage()));

			throw ExceptionUtils.getRootCause(e);
		}
	}

	protected Object select(SelectPaymentRequest request, ResultMatcher resultMatcher) throws Throwable {
		try {
			Object response = perform("/api/payment/" + request.getId(), request, resultMatcher, SelectPaymentResponse.class);

			if (response instanceof SelectPaymentResponse) {
				SelectPaymentResponse searchPaymentResponse = (SelectPaymentResponse) response;
				System.err.println(String.format("Price = %d, vat = %d : 조회 성공", searchPaymentResponse.getPrice(),
						searchPaymentResponse.getVat()));
			}

			return response;
		} catch (Exception e) {
			System.err.println(String.format("조회 실패 (%s)", e.getMessage()));

			throw ExceptionUtils.getRootCause(e);
		}
	}

	private <T> Object perform(String url, T request, ResultMatcher resultMatcher, Class<?> clazz) throws Throwable {
		ResultActions resultActions = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(request)));

		if (resultMatcher != null) {
			resultActions.andExpect(resultMatcher);
		}

		MvcResult result = resultActions.andDo(print()).andReturn();

		HttpStatus httpStatus = HttpStatus.valueOf(result.getResponse().getStatus());

		String content = result.getResponse().getContentAsString();

		if (httpStatus == HttpStatus.OK) {
			return objectMapper.readValue(content, clazz);
		} else {
			return objectMapper.readValue(content, ErrorCode.class);
		}
	}
}