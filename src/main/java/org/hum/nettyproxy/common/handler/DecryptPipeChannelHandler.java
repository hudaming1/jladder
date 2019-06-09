package org.hum.nettyproxy.common.handler;

import org.hum.nettyproxy.common.util.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class DecryptPipeChannelHandler extends ChannelInboundHandlerAdapter {

	@SuppressWarnings("unused")
	private String name;
	private Channel pipeChannel;

	public DecryptPipeChannelHandler(String name, Channel channel) {
		this.name = name;
		this.pipeChannel = channel;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (pipeChannel.isActive()) {
				ByteBuf bytebuff = (ByteBuf) msg; 
				if (!bytebuff.hasArray()) {
					byte[] arr = new byte[bytebuff.readableBytes()];
					try {
						bytebuff.getBytes(0, arr); 
						byte[] decrypt = Utils.decrypt(arr);
						pipeChannel.writeAndFlush(Unpooled.wrappedBuffer(decrypt));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}
}
