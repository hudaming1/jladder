package org.hum.nettyproxy.compoment.auth;

import java.util.HashMap;
import java.util.Map;

public class AuthManager {

	private Map<String, Object> whileList = new HashMap<>();

	public boolean login(String ipaddress, String name, String password) {
		if ("hudaming".equals(name) && "123456".equals(password)) {
			whileList.put(ipaddress, System.currentTimeMillis());
			return true;
		}
		return false;
	}

	public boolean isLogin(String ipaddress) {
		return whileList.containsKey(ipaddress);
	}
}
