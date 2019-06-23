package org.hum.nettyproxy.compoment.interceptor.impl;

import java.util.Map.Entry;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.compoment.interceptor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

/**
 * 实现类似于DNS映射表的功能，但也可以对IP进行映射
 * <pre>
 *    XXX 这个类可否不依赖于Interceptor会更好一些
 * </pre>
 * @author hudaming
 */
public class RedirectProcessor implements Processor {

	private static final Logger logger = LoggerFactory.getLogger(RedirectProcessor.class);
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
			String source = header.getValue();
			// 替换请求行
			if (httpRequest.getLine().contains(source)) {
				httpRequest.setLine(httpRequest.getLine().replace(source, redirectHost));
			}
			// 替换请求头
			header.setValue(redirectHost);
			httpRequest.refreshByteBuf();
			logger.info("redirect {}->{}", source, redirectHost);
			break;
		}
		ctx.fireChannelRead(httpRequest.getByteBuf());
	}
}
