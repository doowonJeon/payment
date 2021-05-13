package com.pay.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.pay.model.CardInfo;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CardRequest extends CardInfo {
	@Size(min = 10, max = 16, message = "카드번호는 10 ~ 16자리여야합니다.")
	@NotNull(message = "card_number는 필수값 입니다.")
	@Pattern(regexp = "[0-9]+", message = "숫자로 입력해주세요.")
	@Override
	public String getCardNumber() {
		return super.getCardNumber();
	}

	@Size(min = 4, max = 4, message = "유효기간은 4자리 형식으로 입력해주세요. (mmyy형식)")
	@NotNull(message = "유효기간은 필수값 입니다.")
	@Override
	public String getValidDate() {
		return super.getValidDate();
	}

	@Size(min = 3, max = 3, message = "cvc 3자리를 입력해주세요.")
	@NotNull(message = "cvc는 필수값 입니다.")
	@Pattern(regexp = "[0-9]+", message = "숫자로 입력해주세요.")
	@Override
	public String getCvc() {
		return super.getCvc();
	}

	@Builder
	public CardRequest(String cardNumber, String validDate, String cvc) {
		super(cardNumber, validDate, cvc);
	}
}
