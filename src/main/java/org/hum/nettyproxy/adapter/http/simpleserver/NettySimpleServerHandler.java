package org.hum.nettyproxy.adapter.http.simpleserver;

import org.hum.nettyproxy.adapter.http.model.HttpRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettySimpleServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		System.out.println(msg);
	}

}
