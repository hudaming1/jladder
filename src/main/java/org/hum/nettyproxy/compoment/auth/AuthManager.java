package org.hum.nettyproxy.compoment.auth;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthManager {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthManager.class);

	private Set<URL> urlWhiteList = new HashSet<URL>();
	private Map<String, Object> userWhileList = new HashMap<>();
	
	public AuthManager() {
		try {
			Integer serverPort = NettyProxyContext.getConfig().getBindHttpServerPort();
			serverPort = serverPort == null ? 80 : serverPort;
			urlWhiteList.add(new URL("HTTP", "127.0.0.1", serverPort, "/login.html"));
			urlWhiteList.add(new URL("HTTP", "localhost", serverPort, "/login.html"));
		} catch (Exception e) {
			logger.error("init url_white_list error", e);
		}
	}

	public boolean login(String ipaddress, String name, String password) {
		if ("hudaming".equals(name) && "123456".equals(password)) {
			userWhileList.put(ipaddress, System.currentTimeMillis());
			return true;
		}
		return false;
	}

	public boolean isLogin(String ipaddress) {
		return userWhileList.containsKey(ipaddress);
	}

	public boolean isUrlInWhilteList(URL url) {
		for (URL _url : urlWhiteList) {
			if (url.equals(_url)) {
				return true;
			}
		}
		return false;
	}
}
