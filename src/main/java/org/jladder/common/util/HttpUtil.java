package org.jladder.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
	/**
	 * 将HTTP请求中的Body Form参数解析成Map
	 * @param body
	 * @return
	 */
	public static Map<String, String> parseBody2FormData(String body) {
		if (body == null || body.trim().isEmpty()) {
			return Collections.emptyMap();
		}
		String[] kvDatas = body.split("&");
		if (kvDatas == null || kvDatas.length == 0) {
			return Collections.emptyMap();
		}
		Map<String, String> params = new HashMap<String, String>();
		for (String kvdata : kvDatas) {
			if (kvdata == null || kvdata.isEmpty()) {
				continue;
			}
			String[] param = kvdata.split("=");
			if (param == null || param.length != 2) {
				continue;
			}
			params.put(param[0], param[1]);
		}
		return params;
	}
	
	public static String parse2RelativeFile(String uri) {
		if (uri.startsWith("http://")) {
			return uri.replaceAll("http://.*?/", "/");
		} else if (uri.startsWith("https://")) {
			return uri.replaceAll("https://.*?/", "/");
		} else if (!uri.contains("/")) {
			return "/"; // HTTPS的Connect请求，URI是域名+端口，因此解析出URI为“/”
		}
		return uri;
	}
}
