package com.pay.exception;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

public class ErrorResponse {
	private ErrorCode error;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object details;

	public ErrorResponse(ErrorCode error, String message, Object details) {
		this.error = error;
		this.message = message;
		this.details = details;
	}

	@JsonGetter
	private String getCode() {
		return error.getCode();
	}

	@JsonGetter
	private String getDescription() {
		return error.getDescription();
	}
}
