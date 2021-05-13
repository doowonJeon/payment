package com.pay.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
public class CancelPaymentRequest {
	@NotNull(message = "id는 필수값 입니다.")
	@Size(min = 20, max = 20, message = "id의 길이는 20 입니다.")
	private String id;

	@Min(value = 100, message = "최소결재금액은 100원 이상이여야 합니다.")
	@Max(value = 1000000000, message = "최대 결재금액은 10억원을 초과할 수 없습니다.")
	@NotNull(message = "결제금액은 필수값 입니다.")
	private Long price;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long vat;

	@Builder
	public CancelPaymentRequest(String id, Long price, Long vat) {
		this.id = id;
		this.price = price;
		this.vat = vat;
	}

	@JsonCreator
	private CancelPaymentRequest() {
		super();
	}
}
