package org.hum.nettyproxy.compoment.interceptor;

import java.util.List;

import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class DefaultInterceptorWrapper implements InterceptorWrapper {

	private static final Logger logger = LoggerFactory.getLogger(DefaultInterceptorWrapper.class);
	
	private InterceptorRegx interceptorRegx;
	private List<Interceptor> interceptorList;
	private Processor processor;
	
	public DefaultInterceptorWrapper() {
	}
	
	public DefaultInterceptorWrapper(InterceptorRegx interceptorRegx) {
		this.interceptorRegx = interceptorRegx;
		// TODO 将regx.match转成interceptor
//		this.interceptor = InterceptorTypeFactory.get(interceptorRegx.getInterceptorType(), interceptorRegx.getInterceptorValue());
//		this.processor = InterceptorTypeFactory.get(interceptorRegx.getProcessType(), interceptorRegx.getProcessValue());
	}

	@Override
	public boolean tryIntercept(HttpRequest httpRequest) {
		if (interceptorList == null) {
			logger.warn("unknown interceptor, iul=" + interceptorRegx.getIul());
			return false;
		}
		// 写死 or 关系，后面需要改成动态的
		for (Interceptor interceptor : interceptorList) {
			return interceptor.isHit(httpRequest);
		}
		return false;
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
