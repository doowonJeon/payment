package com.pay.service;

import com.pay.exception.ApiException;
import com.pay.request.PaymentRequest;
import com.pay.request.SelectPaymentRequest;
import com.pay.response.PaymentResponse;

public interface PaymentService {

	public PaymentResponse registerPayment(PaymentRequest paymentDto) throws ApiException;

}
