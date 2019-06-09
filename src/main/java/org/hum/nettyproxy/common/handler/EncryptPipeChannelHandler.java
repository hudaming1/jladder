package org.hum.nettyproxy.common.handler;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.util.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class EncryptPipeChannelHandler extends ChannelInboundHandlerAdapter {

	@SuppressWarnings("unused")
	private String name;
	private Channel pipeChannel;

	public EncryptPipeChannelHandler(String name, Channel channel) {
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
					bytebuff.getBytes(0, arr);
					try {
						byte[] encrypt = Utils.encrypt(arr);
						ByteBuf buf = ctx.alloc().directBuffer(encrypt.length + 4); // +4是int长度
						buf.writeBytes(encrypt);
						buf.writeBytes(Constant.FIXED_DERTIMED.getBytes());
						pipeChannel.writeAndFlush(buf);
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
