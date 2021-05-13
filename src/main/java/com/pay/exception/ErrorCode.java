package com.pay.exception;

import lombok.Getter;

public enum ErrorCode {
    ERROR("C0001", "error"),
    ENCRYPT("C0002", "ENCRYPT error"),
    DECRYPT("C0003", "DECRYPT error"),
    GET_KEY_FROM_PASSWORD("C0004", "GET_KEY_FROM_PASSWORD error"),
    VAT_WRONG("C0005", "VAT is Wrong"),
    NOT_FOUND("C0006", "Not found Payment"),
    SHORT_PRICE("C0007", "The cancellation price is higher."),
    SHORT_VAT("C0008", "The cancellation vat is higher."),
    VAT_GREATER_THAN_PRICE("C0009", "VAT이 금액보다 큽니다."),
    INVALID_PARAMETER("C0010", "Invalid parameter"),
    INVALID_CANCEL("C0011", "Cancellation is only possible once."),
    INVALID_VAT("C0012","VAT cannot be greater than the amount paid.")
    ;
    

	@Getter
	private String code;

	@Getter
	private String description;

	ErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}
}
