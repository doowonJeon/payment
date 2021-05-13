package com.pay.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ApiException.class)
	@ResponseBody
	protected ResponseEntity<ErrorResponse> exceptionController(ApiException e) {
		return ResponseEntity.badRequest().body(new ErrorResponse(e.getError(), e.getMessage(), null));
	}

	@ExceptionHandler(ApiRuntimeException.class)
	@ResponseBody
	protected ResponseEntity<ErrorResponse> handleApiException(ApiRuntimeException e) {
		return ResponseEntity.badRequest().body(new ErrorResponse(e.getError(), e.getMessage(), null));
	}

}