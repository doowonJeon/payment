package com.pay.service;

import com.pay.exception.ApiException;
import com.pay.request.CancelPaymentRequest;
import com.pay.response.CancelPaymentResponse;

public interface CancelPaymentService {

	public CancelPaymentResponse cancelPayment(CancelPaymentRequest cancelPaymentDto) throws ApiException;

}
