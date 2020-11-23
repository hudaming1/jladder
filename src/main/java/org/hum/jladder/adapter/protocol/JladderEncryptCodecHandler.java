package org.hum.jladder.adapter.protocol;

import org.hum.jladder.adapter.protocol.encrypt.EncStrategy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class JladderEncryptCodecHandler extends ChannelDuplexHandler {
	
	private EncStrategy encStrategy;
	
	public JladderEncryptCodecHandler(EncStrategy encStrategy) {
		this.encStrategy = encStrategy;
	}

	// read -> decrypt
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof ByteBuf) {
    		ctx.fireChannelRead(encStrategy.decrypt((ByteBuf) msg));
    	} else {
    		ctx.fireChannelRead(msg);
    	}
    }
	
	// write -> encrypt
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof JladderMessage) {
    		ctx.alloc().directBuffer();
    		ctx.writeAndFlush(encStrategy.encrypt((JladderMessage) msg));
    	} else {
    		ctx.writeAndFlush(msg);
    	}
    }
}
