package org.jladder.core.serial;

import org.jladder.core.message.JladderMessage;

import io.netty.buffer.ByteBuf;

public interface JladderSerialization {

	public ByteBuf serial(JladderMessage message);

	public JladderMessage deserial(ByteBuf byteBuf);
}
