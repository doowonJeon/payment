package com.pay.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AESUtilTest {

	@Test
	public void Test_1() throws Exception {

		String secret_key = "secretsecret";
		String plain_text = "This character should appear";

		String enc_data = AESUtil.encrypt(plain_text, secret_key);

		String dec_data = AESUtil.decrypt(enc_data, secret_key);

		Assertions.assertEquals(plain_text, dec_data);
	}
}
