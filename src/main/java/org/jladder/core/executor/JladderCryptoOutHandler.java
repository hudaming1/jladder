package org.jladder.core.executor;

import org.jladder.core.message.JladderMessage;
import org.jladder.core.serial.JladderSerialization;
import org.jladder.core.serial.SimpleJladderSerialization;

import io.netty.channel.ChannelHandler.Sharable;
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
    		ctx.writeAndFlush(serialization.serial((JladderMessage) msg));
    	} else {
    		ctx.writeAndFlush(msg);
    	}
    }

}
