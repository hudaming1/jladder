package org.hum.nettyproxy.common.handler;

import org.hum.nettyproxy.common.util.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class EncryptPipeChannelHandler extends ChannelInboundHandlerAdapter {

	private Channel pipeChannel;

	public EncryptPipeChannelHandler(Channel channel) {
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
						ByteBuf buf = ctx.alloc().directBuffer(); 
						buf.writeInt(encrypt.length);
						buf.writeBytes(encrypt);
						// System.out.println("encode.arr=" + Arrays.toString(encrypt));
						System.out.println("encode.len=" + encrypt.length + ", readabled_size=" + buf.readableBytes());
						//System.out.println(Arrays.toString(encrypt));
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
