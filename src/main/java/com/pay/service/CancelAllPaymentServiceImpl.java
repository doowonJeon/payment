package com.pay.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.exception.ApiException;
import com.pay.exception.ErrorCode;
import com.pay.model.Company;
import com.pay.model.Payment;
import com.pay.repository.CompanyRepository;
import com.pay.repository.PaymentRepository;
import com.pay.request.CancelPaymentRequest;
import com.pay.response.CancelPaymentResponse;
import com.pay.util.AESUtil;
import com.pay.util.StringUtil;

@Service("cancelAllPaymentService")
public class CancelAllPaymentServiceImpl implements CancelAllPaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Value("${secret.key}")
	private String SECRET_KEY;

	@Transactional
	@Override
	public CancelPaymentResponse cancelAllPayment(CancelPaymentRequest cancelPaymentDto) throws ApiException {
		Payment payment = paymentRepository.findById(cancelPaymentDto.getId())
				.orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

		if (payment.getCancel_flag()) {
			throw new ApiException(ErrorCode.INVALID_CANCEL);
		}
		long requestVat = checkValid(cancelPaymentDto, payment);
		String dec_card_info = AESUtil.decrypt(payment.getCard_info(), SECRET_KEY);
		payment.setPrice(cancelPaymentDto.getPrice());
		payment.setVat(requestVat);
		payment.setInstallment_months(0);

		String data = StringUtil.setString(payment, "CANCEL", dec_card_info);
		Company company = new Company();
		company.setId(payment.getId());
		company.setData(data);
		companyRepository.save(company);

		payment.setCancel_flag(true);
		paymentRepository.save(payment);
		return CancelPaymentResponse.builder().id(payment.getId()).data(data).build();
	}

	private long checkValid(CancelPaymentRequest cancelPaymentDto, Payment payment) throws ApiException {
		long requestPrice = cancelPaymentDto.getPrice();
		long requestVat;

		if (cancelPaymentDto.getVat() == null) {
			requestVat = BigDecimal.valueOf(requestPrice).divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP)
					.longValue();
		} else {
			requestVat = cancelPaymentDto.getVat();
		}
		return requestVat;
	}

}
