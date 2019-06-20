package org.hum.nettyproxy.compoment.interceptor;

import org.hum.nettyproxy.common.model.HttpRequest;

public interface Interceptor {

	/**
	 * 判断是否能拦截请求
	 * @param httpRequest
	 * @return
	 */
	public boolean isHit(HttpRequest httpRequest);
}
