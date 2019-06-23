package org.hum.nettyproxy.compoment.auth;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 身份验证
 * XXX 目前身份校验没有将http验证抽象出来
 * 扩展考虑：
 * 	如果连接数据库？或对接其他系统？
 * 	身份标识还是写死用IP呢。。。
 * @author hudaming
 */
public class AuthManager {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthManager.class);

	private Set<URL> urlWhiteList = new HashSet<URL>();
	private Map<String, Object> userWhileList = new HashMap<>();
	private static final AuthManager authManager = new AuthManager();
	
	// XXX 待改进
	public static AuthManager getInstance() {
		return authManager;
	}
	
	private AuthManager() {
		try {
			Integer serverPort = NettyProxyContext.getConfig().getBindHttpServerPort();
			String serverUrl = NettyProxyContext.getConfig().getBindHttpServerUrl();
			if (serverUrl != null && serverPort != null) {
				urlWhiteList.add(new URL("HTTP", serverUrl, serverPort, "*"));
			}
			// 本机做proxy时，clientIp相当于管理员，允许登录
			userWhileList.put("0:0:0:0:0:0:0:1", System.currentTimeMillis());
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
			} else if ("*".equals(_url.getFile()) && _url.getProtocol().equalsIgnoreCase(url.getProtocol()) && _url.getHost().equalsIgnoreCase(url.getHost()) || _url.getPort() == url.getPort()) {
				return true;
			}
		}
		return false;
	}
}
