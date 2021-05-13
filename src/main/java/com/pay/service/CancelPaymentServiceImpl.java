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

import lombok.Data;

@Service("cancelPaymentService")
public class CancelPaymentServiceImpl implements CancelPaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Value("${secret.key}")
	private String SECRET_KEY;

	@Transactional
	@Override
	public CancelPaymentResponse cancelPayment(CancelPaymentRequest cancelPaymentDto) throws ApiException {
		Payment payment = paymentRepository.findById(cancelPaymentDto.getId())
				.orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

		ValidResult check_result = checkValid(cancelPaymentDto, payment);
		String dec_card_info = AESUtil.decrypt(payment.getCard_info(), SECRET_KEY);

		payment.setPrice(check_result.getRequestPrice());
		payment.setVat(check_result.getRequestVat());
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

	private ValidResult checkValid(CancelPaymentRequest cancelPaymentDto, Payment payment) throws ApiException {
		long currentVat = payment.getVat();
		long currentPrice = payment.getPrice();

		long requestPrice = cancelPaymentDto.getPrice();
		long requestVat;

		boolean vat_null_flag = false;

		if (cancelPaymentDto.getVat() == null) {
			requestVat = BigDecimal.valueOf(requestPrice).divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP)
					.longValue();
			vat_null_flag = true;
		} else {
			requestVat = cancelPaymentDto.getVat();
		}

		if (requestPrice > currentPrice) {
			throw new ApiException(ErrorCode.SHORT_PRICE);
		}

		if (requestVat > currentVat) {
			if (!vat_null_flag) {
				throw new ApiException(ErrorCode.SHORT_VAT);
			} else {
				if (requestVat > currentVat) {
					requestVat = currentVat;
				}
			}
		}

		if (requestPrice < requestVat) {
			throw new ApiException(ErrorCode.VAT_GREATER_THAN_PRICE);
		}

		if (currentPrice - requestPrice < currentVat - requestVat) {
			throw new ApiException(ErrorCode.VAT_GREATER_THAN_PRICE);
		}

		return new ValidResult(currentPrice - requestPrice, currentVat - requestVat);
	}

	@Data
	private static class ValidResult {
		private long requestPrice;
		private long requestVat;

		ValidResult(long requestPrice, long requestVat) {
			this.requestPrice = requestPrice;
			this.requestVat = requestVat;
		}
	}

}
