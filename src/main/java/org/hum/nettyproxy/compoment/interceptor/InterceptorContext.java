package org.hum.nettyproxy.compoment.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx2;

public class InterceptorContext {
	
	private static final List<InterceptorWrapper> wrapper;
	
	static {
		wrapper = new ArrayList<InterceptorWrapper>();
		List<InterceptorRegx2> interceptorRegxList = NettyProxyContext.getConfig().getInterceptorRegxList();
		// init interceptor-wrapper
		for (InterceptorRegx2 interceptorRegx : interceptorRegxList) {
			wrapper.add(new DefaultInterceptorWrapper(interceptorRegx));
		}
	}
	
	public static List<InterceptorWrapper> getWrappers() {
		return wrapper;
	}

}
