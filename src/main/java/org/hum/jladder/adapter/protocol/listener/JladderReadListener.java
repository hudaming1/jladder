package org.hum.jladder.adapter.protocol.listener;

import org.hum.jladder.adapter.protocol.JladderByteBuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class JladderReadListener extends SimpleChannelInboundHandler<JladderByteBuf> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JladderByteBuf msg) throws Exception {
		this.onRead(msg);
	}

	public abstract void onRead(JladderByteBuf msg);
}
