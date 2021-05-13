package com.pay.exception;

import lombok.Getter;

public enum ErrorCode {
    ERROR("E001", "error"),
    ENCRYPT("E002", "ENCRYPT error"),
    DECRYPT("E003", "DECRYPT error"),
    GET_KEY_FROM_PASSWORD("E004", "GET_KEY_FROM_PASSWORD error"),
    VAT_WRONG("E005", "VAT is Wrong"),
    NOT_FOUND("E006", "Not found Payment"),
    SHORT_PRICE("E007", "The cancellation price is higher."),
    SHORT_VAT("E007", "The cancellation vat is higher."),
    VAT_GREATER_THAN_PRICE("E007", "VAT이 금액보다 큽니다."),
    INVALID_PARAMETER("E008", "Invalid parameter"),
    INVALID_CANCEL("E008", "Cancellation is only possible once."),
    INVALID_VAT("E008","VAT cannot be greater than the amount paid.")
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
