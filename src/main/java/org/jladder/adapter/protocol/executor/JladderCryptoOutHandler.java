package org.jladder.adapter.protocol.executor;

import org.jladder.adapter.protocol.message.JladderMessage;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

@Sharable
public class JladderCryptoOutHandler extends ChannelOutboundHandlerAdapter {
	
	// write -> encrypt
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof JladderMessage) {
    		JladderMessage jladderMessage = (JladderMessage) msg;
    		ctx.writeAndFlush(jladderMessage.toByteBuf());
    	} else {
    		ctx.writeAndFlush(msg);
    	}
    }

}
