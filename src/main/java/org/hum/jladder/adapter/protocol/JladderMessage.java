package org.hum.jladder.adapter.protocol;

import io.netty.buffer.ByteBuf;

public class JladderMessage {

	private long id;
	private boolean needCodec;
	private String host;
	private int port;
	private ByteBuf body;

	private JladderMessage(boolean needCodec, long id, String host, int port, ByteBuf body) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.body = body;
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
	
	public boolean isNeedCodec() {
		return needCodec;
	}

	public static JladderMessage buildNormalMessage(String host, int port, ByteBuf body) {
		return buildNormalMessage(System.nanoTime(), host, port, body);
	}

	public static JladderMessage buildNormalMessage(long id, String host, int port, ByteBuf body) {
		return new JladderMessage(true, id, host, port, body);
	}

	public static JladderMessage buildEncMessage(long id, String host, int port, ByteBuf body) {
		return new JladderMessage(false, id, host, port, body);
	}
}
