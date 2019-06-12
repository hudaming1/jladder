package org.hum.nettyproxy.adapter.http.model;

import java.util.HashMap;
import java.util.Map;

import org.hum.nettyproxy.common.Constant;

import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class HttpRequest {

	private ByteBuf byteBuf;
	private String line;
	private Map<String, String> headers = new HashMap<String, String>();
	private String body;
	private String host;
	private int port;
	private String method;
	
	public boolean isHttps() {
		return Constant.HTTPS_METHOD.equalsIgnoreCase(method);
	}
}
