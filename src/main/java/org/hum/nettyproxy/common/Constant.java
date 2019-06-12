package org.hum.nettyproxy.common;

public class Constant {

	public static final int MAGIC_NUMBER = 1387781;
	public static final String FIXED_DERTIMED = "\r\n\r\n";

	// HTTP
	public static final String HTTPS_METHOD = "CONNECT";
	public static final String HTTP_PROXY_HEADER = "Proxy-Connection";
	public static final String HTTP_HOST_HEADER = "HOST";
	public static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	public static final int DEFAULT_HTTP_PORT = 80;
	public static final int DEFAULT_HTTPS_PORT = 443;
}
