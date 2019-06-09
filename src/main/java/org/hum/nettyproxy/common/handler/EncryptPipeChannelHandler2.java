package org.hum.nettyproxy.common.handler;

import java.util.Arrays;

import org.hum.nettyproxy.common.util.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

public class EncryptPipeChannelHandler2 extends ChannelOutboundHandlerAdapter {

	public EncryptPipeChannelHandler2() {
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		try {
			if (ctx.channel().isActive()) {
				ByteBuf bytebuff = (ByteBuf) msg;
				if (!bytebuff.hasArray()) {
					byte[] arr = new byte[bytebuff.readableBytes()];
					bytebuff.getBytes(0, arr);
					try {
						byte[] encrypt = Utils.encrypt(arr);
						ByteBuf buf = ctx.alloc().directBuffer();
						buf.writeInt(encrypt.length);
						System.out.println("encode.len=" + encrypt.length);
						buf.writeBytes(encrypt);
//						System.out.println("encode.arr=" + Arrays.toString(encrypt));
						ctx.channel().writeAndFlush(buf);
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
