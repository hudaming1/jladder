package org.hum.jladder.adapter.http.wrapper;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class HttpRequestWrapper {

	private FullHttpRequest request;
	private InetAddress address;
	public static final String Host = "Host";
	public static final int DEFAULT_HTTP_PORT = 80;
	public static final int DEFAULT_HTTPS_PORT = 443;
	
	public HttpRequestWrapper(FullHttpRequest request) {
		this.request = request;
		this.address = parse2InetAddress(request);
	}

	public InetAddress parse2InetAddress(HttpRequest request) {
		return parse2InetAddress(request, isHttps());
	}
	
    public InetAddress parse2InetAddress(HttpRequest request, boolean isHttps) {
    	if (request.headers().get(Host) == null) {
    		return null;
    	}
		// read host and port from http-request
		String[] hostAndPort = request.headers().get(Host).split(":");
		String host = hostAndPort[0];
		int port = guessPort(isHttps, hostAndPort);
		return new InetAddress(host, port);
    }
	
	private int guessPort(boolean isHttps, String[] hostAndPort) {
		if (hostAndPort.length == 2) {
			return Integer.parseInt(hostAndPort[1]);
		} else if (isHttps) {
			return DEFAULT_HTTPS_PORT;
		} else {
			return DEFAULT_HTTP_PORT;
		}
	}

	public HttpRequestWrapper header(String key, String value) {
		request.headers().set(key, value);
		return this;
	}
	
	public String host() {
		return address.getHost();
	}
	
	public int port() {
		return address.getPort();
	}
	
	public boolean isHttps() {
		return request.method() == HttpMethod.CONNECT;
	}
	
	public FullHttpRequest toRequest() {
		return request;
	}

	public ByteBuf toByteBuf() {
		// TODO Auto-generated method stub
		return null;
	}

    @Getter
    @AllArgsConstructor
    public static class InetAddress {
    	private String host;
    	private int port;
    	
    	@Override
    	public String toString() {
    		return host + ":" + port;
    	}
    }
}
