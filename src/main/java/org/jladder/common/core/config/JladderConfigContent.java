package org.jladder.common.core.config;

import lombok.Data;

/**
 * NettyProxyConfig内容标准对象
 */
@Data
public class JladderConfigContent {
	private Object runMode;
	private Object port;
	private Object consolePort;
	private Object workerCnt;
	private Object outsideProxyHost;
	private Object webroot;
	private Object enableAuthority = false;
	private Object interceptorRegxList;
}
