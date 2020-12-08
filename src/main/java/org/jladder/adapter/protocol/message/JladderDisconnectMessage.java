package org.jladder.adapter.protocol.message;

import org.jladder.adapter.protocol.enumtype.JladderMessageTypeEnum;

public class JladderDisconnectMessage extends JladderMessage {

	JladderDisconnectMessage(String clientIden, String host, int port) {
		super(JladderMessageTypeEnum.Disconnect.getCode(), clientIden, host, port);
	}
}
