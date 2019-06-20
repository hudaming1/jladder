package org.hum.nettyproxy.compoment.interceptor.impl;

import java.util.Map.Entry;
import java.util.Set;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.compoment.interceptor.Interceptor;

/**
 * 域名拦截器（多个域名或IP，使用逗号分隔）
 * <pre>
 * TODO 目前仅实现了精准匹配，暂时还无法做到配置了顶级域名，能命中二级三级域名的功能。
 * 后续考虑用指针从后向前移动比对，来实现域名匹配。
 * </pre>
 * @author huming
 */
public class HostInterceptor implements Interceptor{
	
	private Set<String> interceptorHost;
	
	public HostInterceptor() { }
	
	public HostInterceptor(String hostArrString) {
		if (hostArrString != null && !hostArrString.trim().isEmpty()) {
			for (String host : hostArrString.split(",")) {
				if (host == null || host.trim().isEmpty()) {
					continue;
				}
				interceptorHost.add(host.trim());
			}
		}
	}

	@Override
	public boolean isHit(HttpRequest httpRequest) {
		for (Entry<String, String> header : httpRequest.getHeaders().entrySet()) {
			if (!Constant.HTTP_HOST_HEADER.equalsIgnoreCase(header.getKey())) {
				continue;
			}
			String requestHost = header.getValue();
			// 目前只是精准匹配
			if (interceptorHost.contains(requestHost)) {
				return true;
			}
		}
		return false;
	}
}
