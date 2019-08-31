package com.foundation.service.basic.web;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foundation.service.basic.common.util.Encryption;
import com.foundation.service.basic.model.ResultModel;
import com.github.mengxianun.core.DataResultSet;
import com.github.mengxianun.core.Translator;
import com.google.common.collect.ImmutableMap;

@RestController
public class AirController {

	private static final String keyiv = "keyiv";

	@Value("${service.air.crypt:false}")
	private boolean crypt;

	@Autowired
	private Translator translator;

	@PostMapping("/action")
	public ResultModel action(@RequestBody String requestJson, HttpSession session) throws Exception {
		if (crypt) {
			String currentKeyiv = getCurrentKey(session);
			requestJson = Encryption.desEncrypt(requestJson, currentKeyiv, currentKeyiv).trim();
		}
		DataResultSet dataResultSet = translator.translate(requestJson);
		if (!dataResultSet.succeed()) {
			return ResultModel.failWithError(dataResultSet.getCode(), dataResultSet.getMessage());
		}
		Object data = dataResultSet.getData();
		if (crypt) {
			String currentKeyiv = getCurrentKey(session);
			data = Encryption.encrypt(data.toString(), currentKeyiv, currentKeyiv);
		}
		return ResultModel.success(data);
	}

	@GetMapping("/action/key")
	public ResultModel getKey(HttpSession session) {
		String newKey = getNewKey();
		session.setAttribute(keyiv, newKey);
		return ResultModel.success(ImmutableMap.of("key", newKey, "iv", newKey));
	}

	private String getCurrentKey(HttpSession session) {
		Object currentKeyiv = session.getAttribute(keyiv);
		if (currentKeyiv == null || currentKeyiv.toString().isEmpty()) {
			currentKeyiv = getNewKey();
			session.setAttribute(keyiv, currentKeyiv.toString());
		}
		return currentKeyiv.toString();
	}

	private String getNewKey() {
		return RandomStringUtils.randomAlphabetic(16);
	}

}
