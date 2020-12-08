package org.jladder.adapter.protocol.message;

import org.jladder.adapter.protocol.enumtype.JladderMessageTypeEnum;

import io.netty.buffer.ByteBuf;

public class JladderDisconnectMessage extends JladderMessage {

	JladderDisconnectMessage(String clientIden, String host, int port) {
		super(JladderMessageTypeEnum.Disconnect.getCode(), clientIden, host, port);
	}

	@Override
	public ByteBuf toByteBuf() {
		// TODO Auto-generated method stub
		return null;
	}
}
