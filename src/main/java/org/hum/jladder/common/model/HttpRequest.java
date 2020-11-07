package org.hum.jladder.common.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hum.jladder.common.Constant;
import org.hum.jladder.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class HttpRequest implements Serializable {

	private static final long serialVersionUID = 657622757435056385L;

	private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);
	
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
	
	public String getProtocol() {
		return isHttps() ? "https" : "http";
	}
	
	public String getUri() {
		if (line == null) {
			logger.warn("cann't parse request-uri, request-line is null");
			return "unknown uri";
		}
		String[] lineSplit = line.split(" ");
		if (lineSplit.length < 2) {
			logger.warn("cann't parse request-uri, request-line is [{}]", line);
			return "unknown uri";
		}
		String url = lineSplit[1];
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
