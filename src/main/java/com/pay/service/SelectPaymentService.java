package com.pay.service;

import com.pay.exception.ApiException;
import com.pay.request.SelectPaymentRequest;
import com.pay.response.SelectPaymentResponse;

public interface SelectPaymentService {

	public SelectPaymentResponse selectPayment(SelectPaymentRequest selectPaymentDto) throws ApiException;

}
