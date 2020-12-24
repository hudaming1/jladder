package org.jladder.adapter.protocol.message;

public abstract class JladderMessage {

	protected final short TRANSFER_TYPE = 0;
	protected final long MAGIC_NUMBER = 0x90ABCDEF;
	protected String clientIden;
	protected int messageType;
	protected String host;
	protected int port;
	protected long msgId;

	JladderMessage(long msgId, int messageType, String clientIden, String host, int port) {
		this.messageType = messageType;
		this.clientIden = clientIden;
		this.host = host;
		this.port = port;
		this.msgId = msgId;
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
	
	public long getMsgId() {
		return msgId;
	}
}
