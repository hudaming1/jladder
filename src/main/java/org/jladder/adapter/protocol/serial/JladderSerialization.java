package org.jladder.adapter.protocol.serial;

import org.jladder.adapter.protocol.message.JladderMessage;

import io.netty.buffer.ByteBuf;

public interface JladderSerialization {

	public ByteBuf serial(JladderMessage message);

	public JladderMessage deserial(ByteBuf byteBuf);
}
