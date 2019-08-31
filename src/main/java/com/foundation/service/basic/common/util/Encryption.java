package com.foundation.service.basic.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
	public static void main(String[] args) {
		try {
			System.out.println(
					Encryption.encrypt("{\"username\":\"admin\",\"password\":\"aigo@2019\",\"captcha\":\"3820\"}"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String KEY = "monitor123123456";

	private static final String IV = "monitor123123456";

	public static String encrypt(String content) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		int blockSize = cipher.getBlockSize();
		byte[] dataBytes = content.getBytes();
		int plaintextLength = dataBytes.length;
		if (plaintextLength % blockSize != 0) {
			plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
		}
		byte[] plaintext = new byte[plaintextLength];
		System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
		SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(), "AES");
		IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
		byte[] encrypted = cipher.doFinal(plaintext);
		return Base64.getEncoder().encodeToString(encrypted);
	}

	public static String encrypt(String content, String key, String iv) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		int blockSize = cipher.getBlockSize();
		byte[] dataBytes = content.getBytes();
		int plaintextLength = dataBytes.length;
		if (plaintextLength % blockSize != 0) {
			plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
		}
		byte[] plaintext = new byte[plaintextLength];
		System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
		SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
		byte[] encrypted = cipher.doFinal(plaintext);
		return Base64.getEncoder().encodeToString(encrypted);
	}

	public static String desEncrypt(String content) throws Exception {
		byte[] encrypted = Base64.getDecoder().decode(content);
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(), "AES");
		IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
		byte[] original = cipher.doFinal(encrypted);
		return new String(original);
	}
	
	public static String desEncrypt(String content, String key, String iv) throws Exception {
		byte[] encrypted = Base64.getDecoder().decode(content);
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
		byte[] original = cipher.doFinal(encrypted);
		return new String(original);
	}

	public static String base64(String content) {		   
		    byte[] contentByte = null;
	        String resultContent = null;
	        try {
	        	contentByte = content.getBytes("utf-8");
	        	if (contentByte != null) {
		        	resultContent = new sun.misc.BASE64Encoder().encode(contentByte);
		        }
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }	        
	        return resultContent;
	}
}