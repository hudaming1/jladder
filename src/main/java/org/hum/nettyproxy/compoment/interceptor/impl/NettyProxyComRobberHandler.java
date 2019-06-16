package org.hum.nettyproxy.compoment.interceptor.impl;

import org.hum.nettyproxy.compoment.interceptor.NamespaceRobberHandler.Interceptor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class NettyProxyComRobberHandler implements Interceptor {

	private static final String Response = "HTTP/1.1 200 OK\r\n"
			+ "Content-type:text/html\r\n"
			+ "\r\n"
			+ "<h1>Hello NettyProxy</h1>\r\n"
			+ "\r\n";
	
	@Override
	public boolean doIntercept(String namespace, ChannelHandlerContext ctx, Object msg) {
		ByteBuf directBuffer = ctx.alloc().directBuffer();
		directBuffer.writeBytes(Response.getBytes());
		ctx.writeAndFlush(directBuffer);
		ctx.channel().close();
		return false;
	}
}
