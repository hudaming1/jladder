package org.jladder.core.executor;

import org.jladder.core.message.JladderMessage;
import org.jladder.core.serial.JladderSerialization;
import org.jladder.core.serial.SimpleJladderSerialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

@Sharable
public class JladderCryptoOutHandler extends ChannelOutboundHandlerAdapter {
	
	private final JladderSerialization serialization = new SimpleJladderSerialization();
	
	// write -> encrypt
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof JladderMessage) {
    		ByteBuf byteBuf = serialization.serial((JladderMessage) msg);
    		System.out.println("server append " + byteBuf.writerIndex() + " bytes");
    		ctx.writeAndFlush(byteBuf);
    		// byteBuf.release();
    	} else {
    		ctx.writeAndFlush(msg);
    	}
		ReferenceCountUtil.release(msg);
    }

}
