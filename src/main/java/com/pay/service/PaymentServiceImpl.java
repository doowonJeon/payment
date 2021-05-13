package com.pay.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.pay.request.CardRequest;
import com.pay.request.PaymentRequest;
import com.pay.request.SelectPaymentRequest;
import com.pay.response.PaymentResponse;
import com.pay.util.AESUtil;
import com.pay.util.StringUtil;

@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Value("${secret.key}")
	private String SECRET_KEY;

	@Transactional
	@Override
	public PaymentResponse registerPayment(PaymentRequest paymentDto) throws ApiException {
		String _cardInfo = setCardInfo(paymentDto);
		Payment payment = setPayment(_cardInfo, paymentDto);
		if (payment == null) {
			return null;
		}
		payment = paymentRepository.save(payment);

		String data = StringUtil.setString(payment, "PAYMENT", _cardInfo);
		Company company = new Company();
		company.setId(payment.getId());
		company.setData(data);
		companyRepository.save(company);

		return PaymentResponse.builder().id(payment.getId()).data(data).build();
	}

	private String setCardInfo(PaymentRequest paymentDto) {
		List<String> list = new ArrayList<String>();
		CardRequest card_info = paymentDto.getCard_info();
		list.add(card_info.getCardNumber());
		list.add(card_info.getValidDate());
		list.add(card_info.getCvc());
		return StringUtils.join(list, "_");
	}

	private Payment setPayment(String _cardInfo, PaymentRequest paymentDto) throws ApiException {
		String enc_card_info = AESUtil.encrypt(_cardInfo, SECRET_KEY);
		Payment payment = new Payment();
		payment.setId(RandomStringUtils.randomAlphanumeric(20));
		payment.setCard_info(enc_card_info);
		payment.setInstallment_months(paymentDto.getInstallmentMonths());
		payment.setPrice(paymentDto.getPrice());
		long vat = 0;
		if (paymentDto.getVat() == null) {
			vat = BigDecimal.valueOf(paymentDto.getPrice()).divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP)
					.longValue();
		} else {
			vat = paymentDto.getVat();
			if (paymentDto.getPrice() < vat) {
				return null;
			}
		}
		payment.setVat(vat);
		return payment;
	}
}
