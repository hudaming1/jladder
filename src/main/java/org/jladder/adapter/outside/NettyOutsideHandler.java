package org.jladder.adapter.outside;

import org.jladder.adapter.protocol.JladderForwardWorker;
import org.jladder.adapter.protocol.JladderMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class NettyOutsideHandler extends SimpleChannelInboundHandler<JladderMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JladderMessage msg) throws Exception {
		// TODO 使用ctx.channel().eventLoop()
		JladderForwardWorker forward = new JladderForwardWorker(msg.getHost(), msg.getPort());
		
//		forward.writeAndFlush(msg.getBody());
		
		
	}
}
