package org.hum.nettyproxy.common.util;

public class StringUtil {

	/**
	 * 截取HTTP的URI的后缀名
	 * @param url e.g:/index.html 或 /index.html?t=1
	 * @return
	 */
	public static String subHttpUriSuffix(String url) {
		if (url == null) {
			return null;
		} else if (url.indexOf(".") <= 0) {
			return url;
		}
		String suffix = url.substring(url.indexOf(".") + 1);
		if (suffix.indexOf("?") > 0) {
			suffix = suffix.substring(0, suffix.indexOf("?"));
		}
		return suffix;
	}
}
