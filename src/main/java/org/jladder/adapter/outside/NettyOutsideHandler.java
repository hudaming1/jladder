package org.jladder.adapter.outside;

import org.jladder.adapter.protocol.JladderMessage;
import org.jladder.adapter.protocol.executor.JladderCryptoForwardWorker;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class NettyOutsideHandler extends SimpleChannelInboundHandler<JladderMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext insideCtx, JladderMessage msg) throws Exception {
		// TODO 使用ctx.channel().eventLoop()
		JladderCryptoForwardWorker forward = new JladderCryptoForwardWorker(msg.getHost(), msg.getPort());
		
		forward.writeAndFlush(msg.getBody());
	}
}
