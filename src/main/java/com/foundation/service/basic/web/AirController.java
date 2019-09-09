package com.foundation.service.basic.web;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foundation.service.basic.common.exception.GlobalException;
import com.foundation.service.basic.common.util.Encryption;
import com.foundation.service.basic.model.ResultModel;
import com.foundation.service.basic.model.ResultModel.ResultStatus;
import com.github.mengxianun.core.DataResultSet;
import com.github.mengxianun.core.Translator;
import com.google.common.collect.ImmutableMap;

@RestController
public class AirController {

	private static final Logger logger = LoggerFactory.getLogger(AirController.class);

	private static final String OLD_KEY_NAME = "oldKey";
	private static final String KEY_NAME = "key";

	@Value("${service.air.crypt:false}")
	private boolean crypt;

	@Autowired
	private Translator translator;

	@PostMapping("/action")
	public ResultModel action(@RequestBody String requestJson, HttpSession session) throws Exception {
		if (crypt) {
			requestJson = decrypt(requestJson, session);
		}
		DataResultSet dataResultSet = translator.translate(requestJson);
		if (!dataResultSet.succeed()) {
			return ResultModel.failWithError(dataResultSet.getCode(), dataResultSet.getMessage());
		}
		Object data = dataResultSet.getData();
		if (crypt) {
			data = encrypt(data, session);
		}
		return ResultModel.success(data);
	}

	@GetMapping("/action/key")
	public ResultModel getKey(HttpSession session) {
		Object currentKeyiv = session.getAttribute(KEY_NAME);
		String newKey = getNewKey();
		session.setAttribute(KEY_NAME, newKey);
		session.setAttribute(OLD_KEY_NAME, String.valueOf(currentKeyiv));
		return ResultModel.success(ImmutableMap.of("key", newKey, "iv", newKey));
	}

	private String encrypt(Object data, HttpSession session) {
		Object currentKeyivValue = session.getAttribute(KEY_NAME);
		try {
			return Encryption.encrypt(data.toString(), currentKeyivValue.toString(), currentKeyivValue.toString());
		} catch (Exception e) {
			logger.error("Request data encrypt failed", e);
			throw new GlobalException(ResultStatus.SERVER_ERROR);
		}
	}

	private String decrypt(String requestJson, HttpSession session) {
		String decrypted = decrypt(requestJson, session.getAttribute(KEY_NAME));
		if (decrypted == null || decrypted.isEmpty()) {
			decrypted = decrypt(requestJson, session.getAttribute(OLD_KEY_NAME));
		}
		if (decrypted == null || decrypted.isEmpty()) {
			throw new GlobalException(ResultStatus.BAD_REQUEST.code(), "Request data decrypt failed");
		}
		return decrypted;
	}

	private String decrypt(String requestJson, Object keyivValue) {
		try {
			String decrypted = Encryption.desEncrypt(requestJson, keyivValue.toString(), keyivValue.toString()).trim();
			if (decrypted.startsWith("{") && decrypted.endsWith("}")) {
				return decrypted;
			}
		} catch (Exception e) {
			logger.error("Request data decrypt failed", e);
			return null;
		}
		return null;
	}

	private String getNewKey() {
		return RandomStringUtils.randomAlphabetic(16);
	}

}
