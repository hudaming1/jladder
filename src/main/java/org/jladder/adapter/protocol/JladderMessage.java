package org.jladder.adapter.protocol;

import io.netty.buffer.ByteBuf;

public class JladderMessage {

	private long id;
	private boolean bodyNeedEncrypt;
	private String host;
	private int port;
	private ByteBuf body;

	private JladderMessage(boolean bodyNeedEncrypt, long id, String host, int port, ByteBuf body) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.body = body;
		this.bodyNeedEncrypt = bodyNeedEncrypt;
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
	
	public boolean isBodyNeedEncrypt() {
		return bodyNeedEncrypt;
	}

	public static JladderMessage buildNeedEncryptMessage(String host, int port, ByteBuf body) {
		return buildNeedEncryptMessage(System.nanoTime(), host, port, body);
	}

	public static JladderMessage buildNeedEncryptMessage(long id, String host, int port, ByteBuf body) {
		return new JladderMessage(true, id, host, port, body);
	}

	public static JladderMessage buildUnNeedEncryptMessage(String host, int port, ByteBuf body) {
		return new JladderMessage(false, System.nanoTime(), host, port, body);
	}
}
