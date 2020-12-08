package org.jladder.adapter.protocol.executor;

import org.jladder.adapter.protocol.message.JladderMessage;
import org.jladder.adapter.protocol.serial.JladderSerialization;
import org.jladder.adapter.protocol.serial.SimpleJladderSerialization;

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
