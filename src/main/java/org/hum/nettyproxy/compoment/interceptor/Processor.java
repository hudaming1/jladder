package org.hum.nettyproxy.compoment.interceptor;

import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.channel.ChannelHandlerContext;

public interface Processor {

	void fireChannelRead(ChannelHandlerContext ctx, HttpRequest httpRequest);
}
