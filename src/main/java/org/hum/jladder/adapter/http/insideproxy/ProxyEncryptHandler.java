package org.hum.jladder.adapter.http.insideproxy;

import org.hum.jladder.common.handler.EncryptPipeChannelHandler.Encryptor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ProxyEncryptHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof ByteBuf) {
    		ByteBuf byteBuf = (ByteBuf) msg;
			byte[] arr = new byte[byteBuf.readableBytes()];
			byteBuf.getBytes(0, arr);
    		Encryptor.encrypt(ctx.alloc().directBuffer(), arr);
    		ctx.write(ctx.alloc().directBuffer(), promise);
    	} else {
    		ctx.write(msg, promise);
    	}
    }
}
