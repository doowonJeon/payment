package com.pay.exception;

import lombok.Getter;

public class ApiRuntimeException extends RuntimeException {

	@Getter
	private ErrorCode error;

	public ApiRuntimeException(ErrorCode error) {
		this(error, error.getDescription());
	}

	public ApiRuntimeException(ErrorCode error, String message) {
		super(message);
		this.error = error;
	}
}