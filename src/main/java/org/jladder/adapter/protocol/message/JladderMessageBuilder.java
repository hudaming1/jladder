package org.jladder.adapter.protocol.message;

import io.netty.buffer.ByteBuf;

public class JladderMessageBuilder {

	public static JladderDataMessage buildNeedEncryptMessage(String clientIden, String host, int port, ByteBuf body) {
		return new JladderDataMessage(clientIden, true, host, port, body);
	}

	public static JladderDataMessage buildUnNeedEncryptMessage(String clientIden, String host, int port, ByteBuf body) {
		return new JladderDataMessage(clientIden, false, host, port, body);
	}

	public static JladderDisconnectMessage buildDisconnectMessage(String clientIden) {
		return new JladderDisconnectMessage(clientIden, null, 0);
	}
}
