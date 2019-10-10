package org.hum.nettyproxy.common.core.config;

import java.util.Map;

import lombok.Data;

@Data
public class CaptureHttpsConfig {

	/**
	 * 域名映射(key-原host；value-转发host)
	 */
	private Map<String, String> hostMapping;
	
	/**
	 * URL映射(key-原URL；value-重定向URL)
	 */
	private Map<String, String> urlMapping;
	
	/**
	 * 针对某一个特定的URL请求（完整路径）Mock假数据
	 * <pre>
	 *    key-URL
	 * </pre>
	 */
	private Map<String, String> mockRespongMapping;
}
