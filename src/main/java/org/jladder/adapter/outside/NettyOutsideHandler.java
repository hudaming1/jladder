package org.jladder.adapter.outside;

import org.jladder.adapter.protocol.JladderMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyOutsideHandler extends SimpleChannelInboundHandler<JladderMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JladderMessage msg) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
