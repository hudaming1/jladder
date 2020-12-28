package org.jladder.core.message;

import org.jladder.core.enumtype.JladderMessageTypeEnum;

public class JladderDisconnectMessage extends JladderMessage {

	JladderDisconnectMessage(long msgId, String clientIden, String host, int port) {
		super(msgId, JladderMessageTypeEnum.Disconnect.getCode(), clientIden, host, port);
	}
}
