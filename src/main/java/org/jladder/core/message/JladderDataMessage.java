package org.jladder.core.message;

import org.jladder.core.enumtype.JladderMessageTypeEnum;

import io.netty.buffer.ByteBuf;

public class JladderDataMessage extends JladderMessage {

	private boolean bodyNeedEncrypt;
	private ByteBuf body;

	JladderDataMessage(long msgId, String clientIden, boolean bodyNeedEncrypt, String host, int port, ByteBuf body) {
		super(msgId, JladderMessageTypeEnum.Data.getCode(), clientIden, host, port);
		this.body = body;
		this.bodyNeedEncrypt = bodyNeedEncrypt;
	}

	public ByteBuf getBody() {
		return body;
	}
	
	public boolean isBodyNeedEncrypt() {
		return bodyNeedEncrypt;
	}
	
}
