package org.hum.nettyproxy.common.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.util.HttpUtil;

import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class HttpRequest {

	private ByteBuf byteBuf;
	private String line;
	private Map<String, String> headers = new HashMap<String, String>();
	// TODO 后续需要改成byte存储
	private String body;
	private String host;
	private int port;
	private String method;
	
	public boolean isHttps() {
		return Constant.HTTPS_METHOD.equalsIgnoreCase(method);
	}
	
	public String getUri() {
		String url = line.split(" ")[1];
		return HttpUtil.parse2RelativeFile(url);
	}
	
	public URL toUrl() throws MalformedURLException {
		return new URL("HTTP", host, port, getUri());
	}
	
	public HttpRequest buildHeader(String header, String value) {
		headers.put(header, value);
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder sbuilder = new StringBuilder();
		
		// request-line
		sbuilder.append(line).append(Constant.RETURN_LINE);
		// request-headers
		for (Entry<String, String> header : headers.entrySet()) {
			sbuilder.append(header.getKey()).append(":").append(header.getValue()).append(Constant.RETURN_LINE);
		}
		//
		sbuilder.append(Constant.RETURN_LINE);
		sbuilder.append(body);
		sbuilder.append(Constant.RETURN_LINE);

		return sbuilder.toString();
	}
	
	public ByteBuf refreshByteBuf() {
		byteBuf.clear().writeBytes(toString().getBytes());
		return byteBuf;
	}
}
