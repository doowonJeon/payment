package com.pay.request;

import org.junit.Test;

import com.pay.common.CommonTestCase;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CardRequestTest extends CommonTestCase {
	@Test
	public void 카드_번호_누락() {
		CardRequest request = defaultCardRequest();
		request.setCardNumber(null);
		assertConstraint(request, NotNull.class);
	}

	@Test
	public void 카드_번호_길이부족() {
		CardRequest request = defaultCardRequest();
		request.setCardNumber("12345");
		assertConstraint(request, Size.class);
	}

	@Test
	public void 카드_번호_길이초과() {
		CardRequest request = defaultCardRequest();
		request.setCardNumber("12345678901234567889012345");
		assertConstraint(request, Size.class);
	}

	@Test
	public void 카드_유효기간_누락() {
		CardRequest request = defaultCardRequest();
		request.setValidDate(null);
		assertConstraint(request, NotNull.class);
	}

	@Test
	public void 카드_유효기간_잘못된_패턴() {
		CardRequest request = defaultCardRequest();
		request.setValidDate("abcd");
//		assertConstraint(request, DateFormat.class);
	}

	@Test
	public void 카드_CVC_누락() {
		CardRequest request = defaultCardRequest();
		request.setCvc(null);
		assertConstraint(request, NotNull.class);
	}

	@Test
	public void 카드_CVC_잘못된_패턴() {
		CardRequest request = defaultCardRequest();
		request.setCvc("abc");
		assertConstraint(request, Pattern.class);
	}

	@Test
	public void 카드_CVC_잘못된_길이() {
		CardRequest request = defaultCardRequest();
		request.setCardNumber("12345");
		assertConstraint(request, Size.class);
	}
}