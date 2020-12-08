package org.jladder.adapter.protocol.message;

import io.netty.buffer.ByteBuf;

public abstract class JladderMessage {

	protected final short TRANSFER_TYPE = 0;
	protected final long MAGIC_NUMBER = 0x90ABCDEF;
	private String clientIden;
	private int messageType;
	private String host;
	private int port;

	JladderMessage(int messageType, String clientIden, String host, int port) {
		this.messageType = messageType;
		this.clientIden = clientIden;
		this.host = host;
		this.port = port;
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

	public int getMessageType() {
		return this.messageType;
	}
	
	public abstract ByteBuf toByteBuf();
}
