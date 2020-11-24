package org.jladder.adapter.outside;

import org.jladder.adapter.protocol.JladderAsynHttpClient;
import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderMessage;
import org.jladder.adapter.protocol.JladderMessageReceiveEvent;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class NettyOutsideHandler extends SimpleChannelInboundHandler<JladderMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext insideCtx, JladderMessage msg) throws Exception {
		// TODO 使用ctx.channel().eventLoop()
		JladderAsynHttpClient client = new JladderAsynHttpClient(msg.getHost(), msg.getPort());
		client.writeAndFlush(msg.getBody()).onReceive(new JladderMessageReceiveEvent() {
			@Override
			public void onReceive(JladderByteBuf byteBuf) {
				insideCtx.writeAndFlush(byteBuf.toByteBuf());
			}
		});
	}
}
