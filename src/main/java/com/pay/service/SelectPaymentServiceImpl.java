package com.pay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.exception.ApiException;
import com.pay.model.CardInfo;
import com.pay.model.Payment;
import com.pay.repository.PaymentRepository;
import com.pay.request.SelectPaymentRequest;
import com.pay.response.SelectPaymentResponse;
import com.pay.util.AESUtil;
import com.pay.util.StringUtil;

@Service("selectPaymentService")
public class SelectPaymentServiceImpl implements SelectPaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Value("${secret.key}")
	private String SECRET_KEY;

	@Transactional
	@Override
	public SelectPaymentResponse selectPayment(SelectPaymentRequest selectPaymentDto) throws ApiException {
		Payment payment = paymentRepository.findById(selectPaymentDto.getId()).get();
		String dec_card_info = AESUtil.decrypt(payment.getCard_info(), SECRET_KEY);
		String[] card_info_arr = dec_card_info.split("_");
		CardInfo cardInfo = new CardInfo();

		String card_number = StringUtil.setNumber(card_info_arr[0]);
		cardInfo.setCardNumber(card_number);
		cardInfo.setValidDate(card_info_arr[1]);
		cardInfo.setCvc(card_info_arr[2]);
		String type = "";

		if (payment.getCancel_flag()) {
			type = "CANCEL";
		} else {
			type = "PAYMENT";
		}

		return SelectPaymentResponse.builder().id(payment.getId()).card_info(cardInfo).type(type)
				.price(payment.getPrice()).vat(payment.getVat()).build();
	}

}
