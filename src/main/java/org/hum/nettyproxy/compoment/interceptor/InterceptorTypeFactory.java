package org.hum.nettyproxy.compoment.interceptor;

import org.hum.nettyproxy.common.enumtype.InterceptorProcessEnum;
import org.hum.nettyproxy.common.enumtype.InterceptorRequestEnum;
import org.hum.nettyproxy.compoment.interceptor.impl.HostInterceptor;
import org.hum.nettyproxy.compoment.interceptor.impl.RedirectProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterceptorTypeFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(InterceptorTypeFactory.class);

	public static Interceptor get(InterceptorRequestEnum interceptorType, String interceptorValue) {
		if (interceptorType == InterceptorRequestEnum.Host) {
			return new HostInterceptor(interceptorValue);
		}
		// TODO
		logger.warn("unknown interceptor-type=" + interceptorType + ", value=" + interceptorType);
		return null;
	}

	public static Processor get(InterceptorProcessEnum processType, String processValue) {
		if (processType == InterceptorProcessEnum.Redirect) {
			return new RedirectProcessor(processValue);
		}
		return null;
	}

}
