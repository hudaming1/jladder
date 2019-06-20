package org.hum.nettyproxy.compoment.interceptor.impl;

import java.util.Map.Entry;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.compoment.interceptor.Processor;

import io.netty.channel.ChannelHandlerContext;

public class RedirectProcessor implements Processor {
	
	private String redirectHost;
	
	public RedirectProcessor() { }
	
	public RedirectProcessor(String redirectHost) {
		this.redirectHost = redirectHost;
	}

	@Override
	public void fireChannelRead(ChannelHandlerContext ctx, HttpRequest httpRequest) {
		
		for (Entry<String, String> header : httpRequest.getHeaders().entrySet()) {
			if (!Constant.HTTP_HOST_HEADER.equalsIgnoreCase(header.getKey())) {
				continue;
			}
			header.setValue(redirectHost);
			// TODO 还差一点实现
			httpRequest.setByteBuf(httpRequest.toByteBuf());
		}
		ctx.fireChannelRead(httpRequest);
	}
}
