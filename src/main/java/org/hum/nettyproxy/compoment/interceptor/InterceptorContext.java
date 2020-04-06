package org.hum.nettyproxy.compoment.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx;

public class InterceptorContext {
	
	private static final List<InterceptorWrapper> wrapper;
	
	static {
		wrapper = new ArrayList<InterceptorWrapper>();
		List<InterceptorRegx> interceptorRegxList = NettyProxyContext.getConfig().getInterceptorRegxList();
		// init interceptor-wrapper
		for (InterceptorRegx interceptorRegx : interceptorRegxList) {
			wrapper.add(new DefaultInterceptorWrapper(interceptorRegx));
		}
	}
	
	public static List<InterceptorWrapper> getWrappers() {
		return wrapper;
	}

}
