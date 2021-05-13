package com.pay.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pay.exception.ApiException;
import com.pay.exception.ApiParameterException;
import com.pay.request.CancelPaymentRequest;
import com.pay.request.PaymentRequest;
import com.pay.request.SelectPaymentRequest;
import com.pay.response.CancelPaymentResponse;
import com.pay.response.PaymentResponse;
import com.pay.response.SelectPaymentResponse;
import com.pay.service.CancelAllPaymentService;
import com.pay.service.CancelPaymentService;
import com.pay.service.PaymentService;
import com.pay.service.SelectPaymentService;

@RestController
@RequestMapping("/api")
public class PayController {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private CancelPaymentService cancelpaymentService;
	
	@Autowired
	private CancelAllPaymentService cancelAllpaymentService;

	@Autowired
	private SelectPaymentService selectPaymentService;

	@PostMapping(value = "/payment")
	public Object payment(@RequestBody @Valid PaymentRequest paymentDto, BindingResult bindingResult)
			throws ApiException {
		if (bindingResult.hasErrors()) {
			throw new ApiParameterException(bindingResult);
		} else {
			PaymentResponse paymentResponse = paymentService.registerPayment(paymentDto);
			if (paymentResponse == null) {
				return ResponseEntity.badRequest().build();
			}
			return paymentResponse;
		}
	}

	@PostMapping(value = "/cancel/all")
	public Object cancelAll(@RequestBody @Valid CancelPaymentRequest cancelPaymentDto, BindingResult bindingResult)
			throws ApiException {
		if (bindingResult.hasErrors()) {
			throw new ApiParameterException(bindingResult);
		} else {
			CancelPaymentResponse cancelPaymentResponse = cancelAllpaymentService.cancelAllPayment(cancelPaymentDto);
			return cancelPaymentResponse;
		}
	}
	
	@PostMapping(value = "/cancel")
	public Object cancel(@RequestBody @Valid CancelPaymentRequest cancelPaymentDto, BindingResult bindingResult)
			throws ApiException {
		if (bindingResult.hasErrors()) {
			throw new ApiParameterException(bindingResult);
		} else {
			CancelPaymentResponse cancelPaymentResponse = cancelpaymentService.cancelPayment(cancelPaymentDto);
			return cancelPaymentResponse;
		}
	}

	@GetMapping(value = "/payment/{id}")
	public Object select(@Valid SelectPaymentRequest selectPaymentDto, BindingResult bindingResult)
			throws ApiException {
		if (bindingResult.hasErrors()) {
			throw new ApiParameterException(bindingResult);
		} else {
			SelectPaymentResponse selectPaymentResponse = selectPaymentService.selectPayment(selectPaymentDto);
			if (selectPaymentResponse == null) {
				return ResponseEntity.badRequest().build();
			}
			return selectPaymentResponse;
		}
	}
}
