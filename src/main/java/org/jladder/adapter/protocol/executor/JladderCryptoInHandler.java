package org.jladder.adapter.protocol.executor;

import java.util.List;

import org.jladder.adapter.protocol.message.JladderMessage;
import org.jladder.adapter.protocol.serial.JladderSerialization;
import org.jladder.adapter.protocol.serial.SimpleJladderSerialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class JladderCryptoInHandler extends ReplayingDecoder<JladderMessage> {
	
	private final JladderSerialization jladderSerialization = new SimpleJladderSerialization();

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		out.add(jladderSerialization.deserial(in));
	}
}
