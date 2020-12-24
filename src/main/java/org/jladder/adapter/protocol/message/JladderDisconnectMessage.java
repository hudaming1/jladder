package org.jladder.adapter.protocol.message;

import org.jladder.adapter.protocol.enumtype.JladderMessageTypeEnum;

public class JladderDisconnectMessage extends JladderMessage {

	JladderDisconnectMessage(long msgId, String clientIden, String host, int port) {
		super(msgId, JladderMessageTypeEnum.Disconnect.getCode(), clientIden, host, port);
	}
}
