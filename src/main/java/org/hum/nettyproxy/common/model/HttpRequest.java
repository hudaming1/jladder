package org.hum.nettyproxy.common.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hum.nettyproxy.common.Constant;

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
		return line.split(" ")[1];
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
