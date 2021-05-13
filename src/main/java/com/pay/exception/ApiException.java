package com.pay.exception;

import lombok.Getter;

public class ApiException extends Exception {
	@Getter
	private ErrorCode error;

	public ApiException(ErrorCode error) {
		this(error, null);
	}

	public ApiException(ErrorCode error, String message) {
		super(message);
		this.error = error;
	}
}
