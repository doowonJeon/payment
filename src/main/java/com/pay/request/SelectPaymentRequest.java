package com.pay.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Builder;
import lombok.Data;

@Data
public class SelectPaymentRequest {
	@NotNull(message = "id는 필수값 입니다.")
	@Size(min = 20, max = 20, message = "id의 길이는 20 입니다.")
	private String id;

	@Builder
	public SelectPaymentRequest(String id) {
		this.id = id;
	}

	@JsonCreator
	private SelectPaymentRequest() {
		super();
	}
}
