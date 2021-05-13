package com.pay.util;

import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.pay.exception.ApiException;
import com.pay.exception.ErrorCode;

public class AESUtil {

	private static final byte[] IV = "kakaoaesutil2021".getBytes(); // 첫 블럭과 XOR 연산을 해야되기 때문에 iv의 길이는 블럭 사이즈인 16 byte
	private static final String ALGO = "AES/CBC/PKCS5Padding";
	private static final String SALT = "-ZHkk{,*jbU78/?s7Ew7l@BcG:L]OosMo:i/%ut@=cydO9No2z)sP@&@Xa137e";

	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(n);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}

	public static SecretKey getKeyFromPassword(String SECRET_KEY) throws ApiException {

		SecretKeyFactory factory;
		SecretKey secret = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
			secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		} catch (Exception e) {
			throw new ApiException(ErrorCode.GET_KEY_FROM_PASSWORD, e.getLocalizedMessage());
		}
		return secret;
	}

	public static String encrypt(String input, String SECRET_KEY) throws ApiException {

		SecretKey key = getKeyFromPassword(SECRET_KEY);
		byte[] cipherText = null;

		try {
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
			cipherText = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			throw new ApiException(ErrorCode.ENCRYPT, e.getLocalizedMessage());
		}
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decrypt(String cipherText, String SECRET_KEY) throws ApiException {

		SecretKey key = getKeyFromPassword(SECRET_KEY);
		byte[] plainText = null;

		try {
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
			plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		} catch (Exception e) {
			throw new ApiException(ErrorCode.DECRYPT, e.getLocalizedMessage());
		}
		return new String(plainText);
	}
}
