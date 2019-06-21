package org.hum.nettyproxy.compoment.interceptor.model;

import org.hum.nettyproxy.common.enumtype.InterceptorRequestEnum;
import org.hum.nettyproxy.common.enumtype.InterceptorProcessEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterceptorRegx {

	private InterceptorRequestEnum interceptorType;
	// 根据type不同，value标识含义也不同（通过type+value就可以准确描述出要拦截的请求了）
	private String interceptorValue;
	// 响应类型
	private InterceptorProcessEnum processType;
	// 响应数据
	private String processValue;
}
