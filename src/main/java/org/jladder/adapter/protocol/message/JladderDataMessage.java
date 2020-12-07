package org.jladder.adapter.protocol.message;

import io.netty.buffer.ByteBuf;

public class JladderDataMessage extends JladderMessage {

	private String clientIden;
	private boolean bodyNeedEncrypt;
	private String host;
	private int port;
	private ByteBuf body;

	JladderDataMessage(String clientIden, boolean bodyNeedEncrypt, String host, int port, ByteBuf body) {
		super(clientIden, host, port);
		this.body = body;
		this.bodyNeedEncrypt = bodyNeedEncrypt;
	}
	
	public String getClientIden() {
		return clientIden;
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
}
