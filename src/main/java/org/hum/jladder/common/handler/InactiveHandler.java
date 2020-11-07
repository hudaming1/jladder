package org.hum.jladder.common.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InactiveHandler extends ChannelInboundHandlerAdapter {

	private Channel needNoticeCloseChannel;
	
	public InactiveHandler(Channel channel) {
		this.needNoticeCloseChannel = channel;
	}
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        if (needNoticeCloseChannel.isActive()) {
        	needNoticeCloseChannel.close();
        }
    }
}