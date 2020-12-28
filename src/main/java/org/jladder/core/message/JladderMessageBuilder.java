package org.jladder.core.message;

import io.netty.buffer.ByteBuf;

public class JladderMessageBuilder {

	public static JladderDataMessage buildNeedEncryptMessage(long msgId, String clientIden, String host, int port, ByteBuf body) {
		return new JladderDataMessage(msgId, clientIden, true, host, port, body);
	}

	public static JladderDataMessage buildUnNeedEncryptMessage(long msgId, String clientIden, String host, int port, ByteBuf body) {
		return new JladderDataMessage(msgId, clientIden, false, host, port, body);
	}

	public static JladderDisconnectMessage buildDisconnectMessage(long msgId, String clientIden) {
		return new JladderDisconnectMessage(msgId, clientIden, "", 0);
	}
}
