package org.hum.jladder.adapter.protocol;

import io.netty.buffer.ByteBuf;

public class JladderMessage {

	private long id;
	private String host;
	private int port;
	private ByteBuf body;

	public JladderMessage(String host, int port, ByteBuf body) {
		this.host = host;
		this.port = port;
		this.body = body;
	}

	public JladderMessage(long id, String host, int port, ByteBuf body) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.body = body;
	}
	
	void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public ByteBuf getBody() {
		return body;
	}
}
