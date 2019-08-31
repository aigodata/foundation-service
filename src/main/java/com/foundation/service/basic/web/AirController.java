package com.foundation.service.basic.web;

import java.util.concurrent.atomic.AtomicReference;

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

	private static AtomicReference<String> keyiv = new AtomicReference<>();

	@Value("${service.air.crypt:false}")
	private boolean crypt;

	@Autowired
	private Translator translator;

	@PostMapping("/action")
	public ResultModel action(@RequestBody String requestJson) throws Exception {
		String currentKeyiv = keyiv.get();
		if (crypt) {
			requestJson = Encryption.desEncrypt(requestJson, currentKeyiv, currentKeyiv).trim();
		}
		DataResultSet dataResultSet = translator.translate(requestJson);
		if (!dataResultSet.succeed()) {
			return ResultModel.failWithError(dataResultSet.getCode(), dataResultSet.getMessage());
		}
		Object data = dataResultSet.getData();
		if (crypt) {
			data = Encryption.encrypt(data.toString(), currentKeyiv, currentKeyiv);
		}
		return ResultModel.success(data);
	}

	@GetMapping("/action/key")
	public ResultModel getKey() {
		keyiv.set(RandomStringUtils.randomAlphabetic(16));
		return ResultModel.success(ImmutableMap.of("key", keyiv.get(), "iv", keyiv.get()));
	}

}
