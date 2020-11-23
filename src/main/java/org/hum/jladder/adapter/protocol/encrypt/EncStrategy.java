package org.hum.jladder.adapter.protocol.encrypt;

import org.hum.jladder.adapter.protocol.JladderMessage;

import io.netty.buffer.ByteBuf;

public interface EncStrategy {

	ByteBuf encrypt(JladderMessage message);
	
	JladderMessage decrypt(ByteBuf byteBuf);
}
