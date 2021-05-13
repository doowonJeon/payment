package com.pay.request;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class PaymentRequest {
	@NotNull(message = "카드정보는 필수값 입니다.")
	@Valid
	public CardRequest card_info;

	@Min(value = 0, message = "일시불의 경우 0, 할부의 경우 최소 1을 입력해주세요.")
	@Max(value = 12, message = "할부는 최대 12개월까지 가능합니다.")
	@NotNull(message = "할부개월수는 필수값 입니다.")
	public Integer installmentMonths;

	@Min(value = 100, message = "최소결재금액은 100원 이상이여야 합니다.")
	@Max(value = 1000000000, message = "최대 결재금액은 10억원을 초과할 수 없습니다.")
	@NotNull(message = "결제금액은 필수값 입니다.")
	public Long price;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Long vat;

	@Builder
	public PaymentRequest(CardRequest card_info, Integer installmentMonths, Long price, Long vat) {
		this.price = price;
		this.vat = vat;
		this.card_info = card_info;
		this.installmentMonths = installmentMonths;
	}
	
	@JsonCreator
	public PaymentRequest() {
        super();
    }
}
