package com.pay.response;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Builder;
import lombok.Data;

@Data
public class PaymentResponse {
	private String id;
	private String data;

	@Builder
	public PaymentResponse(String id, String data) {
		this.id = id;
		this.data = data;
	}

	@JsonCreator
	private PaymentResponse() {
		super();
	}

}
