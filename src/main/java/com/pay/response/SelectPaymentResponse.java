package com.pay.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.pay.model.CardInfo;

import lombok.Builder;
import lombok.Data;

@Data
public class SelectPaymentResponse {
	private String id;

	private CardInfo card_info;

	private String type;

	private Long price;

	private Long vat;

	@Builder
	public SelectPaymentResponse(String id, CardInfo card_info, String type, Long price, Long vat) {
		this.id = id;
		this.card_info = card_info;
		this.type = type;
		this.price = price;
		this.vat = vat;
	}

	@JsonCreator
	private SelectPaymentResponse() {
		super();
	}

}
