package com.pay.util;

import org.apache.commons.lang3.StringUtils;

import com.pay.model.Payment;

public class StringUtil {
	public static String setString(Payment payment, String type, String cardInfo) {
		String[] card_info_arr = cardInfo.split("_");

		String header_leng = lnumber(446, 4);
		String header_type = rstring(type, 10);
		String header_num = rstring(payment.getId(), 20);

		String data_cardnum = rnumber(card_info_arr[0], 20);
		String data_installment = lnumber_zero(payment.getInstallment_months(), 2);
		String data_validdate = rnumber(card_info_arr[1], 4);
		String data_cvc = rnumber(card_info_arr[2], 3);
		String data_price = lnumber(payment.getPrice(), 10);
		String data_vat = lnumber_zero(payment.getVat(), 10);
		String data_managenum = rstring("", 20);
		if (type == "CANCEL") {
			data_managenum = rstring(payment.getId(), 20);
		}
		String data_encdata = rstring(payment.getCard_info(), 300);
		String data_pause = rstring("", 47);

		String string_data = new StringBuilder(header_leng).append(header_type).append(header_num).append(data_cardnum)
				.append(data_installment).append(data_validdate).append(data_cvc).append(data_price).append(data_vat)
				.append(data_managenum).append(data_encdata).append(data_pause).toString();
		return string_data;

	}

	public static String lnumber(Object input, int length) {
		return StringUtils.leftPad(String.valueOf(input), length, " ");
	}

	public static String lnumber_zero(Object input, int length) {
		return StringUtils.leftPad(String.valueOf(input), length, "0");
	}

	public static String rnumber(Object input, int length) {
		return StringUtils.rightPad(String.valueOf(input), length, " ");
	}

	public static String rstring(Object input, int length) {
		return StringUtils.rightPad(String.valueOf(input), length, " ");
	}

	public static String setNumber(String number) {
		int start = 3;
		int end = Math.min(number.length(), start + (number.length() - 6));

		String set_number = new StringBuilder().append(StringUtils.repeat("*", 3))
				.append(StringUtils.substring(number, start, end)).append(StringUtils.repeat("*", 3)).toString();
		return set_number;
	}
}
