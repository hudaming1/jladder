package org.hum.nettyproxy.common.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InactiveHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(InactiveHandler.class);
	private Channel needNoticeCloseChannel;
	
	public InactiveHandler(Channel channel) {
		this.needNoticeCloseChannel = channel;
	}
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	logger.info("remote-server[{}<->{}] is closed!", ctx.channel().remoteAddress(), needNoticeCloseChannel.remoteAddress());
        ctx.fireChannelInactive();
        if (needNoticeCloseChannel.isActive()) {
        	needNoticeCloseChannel.close();
        }
    }
}