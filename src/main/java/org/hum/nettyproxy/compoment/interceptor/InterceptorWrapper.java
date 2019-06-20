package org.hum.nettyproxy.compoment.interceptor;

import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.channel.ChannelHandlerContext;

public interface InterceptorWrapper {

	boolean tryIntercept(HttpRequest httpRequest);

	void doProcess(ChannelHandlerContext ctx, HttpRequest httpRequest);
}
