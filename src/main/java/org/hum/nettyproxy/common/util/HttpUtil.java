package org.hum.nettyproxy.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

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
}
