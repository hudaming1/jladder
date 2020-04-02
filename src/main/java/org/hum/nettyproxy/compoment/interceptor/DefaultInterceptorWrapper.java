package org.hum.nettyproxy.compoment.interceptor;

import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class DefaultInterceptorWrapper implements InterceptorWrapper {

	private static final Logger logger = LoggerFactory.getLogger(DefaultInterceptorWrapper.class);
	
	private InterceptorRegx2 interceptorRegx;
	private Interceptor interceptor;
	private Processor processor;
	
	public DefaultInterceptorWrapper() {
	}
	
	public DefaultInterceptorWrapper(InterceptorRegx2 interceptorRegx) {
		this.interceptorRegx = interceptorRegx;
		this.interceptor = InterceptorTypeFactory.get(interceptorRegx.getInterceptorType(), interceptorRegx.getInterceptorValue());
		this.processor = InterceptorTypeFactory.get(interceptorRegx.getProcessType(), interceptorRegx.getProcessValue());
	}

	@Override
	public boolean tryIntercept(HttpRequest httpRequest) {
		if (interceptor == null) {
			logger.warn("unknown interceptor enum-type=" + interceptorRegx.getInterceptorType());
			return false;
		}
		return interceptor.isHit(httpRequest);
	}

	@Override
	public void doProcess(ChannelHandlerContext ctx, HttpRequest httpRequest) {
		if (processor == null) {
			ctx.fireChannelRead(httpRequest.getByteBuf());
			return ;
		}
		processor.fireChannelRead(ctx, httpRequest);
	}
}
