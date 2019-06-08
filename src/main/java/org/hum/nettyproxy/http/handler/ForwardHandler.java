package org.hum.nettyproxy.http.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * remote -> local
 * @author hudaming
 */
public class ForwardHandler extends ChannelInboundHandlerAdapter {
	private Channel channel;
	
	public ForwardHandler(Channel channel) {
		this.channel = channel;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext remoteCtx, Object msg) throws Exception {
    	// forward response
    	this.channel.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        channel.close();
    }
}