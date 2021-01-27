package org.jladder.config;

import org.jladder.common.enumtype.RunModeEnum;

import lombok.Data;

@Data
public class JladderFullConfig {

	/**
	 * 运行模式：根据枚举选择程序做什么样的转发
	 * 启动参数样例：nettyproxy.runmode=11
	 */
	private RunModeEnum runMode;
	/**
	 * 服务启动监听端口，代理程序需要将流量转发到这个端口方可实现转发
	 * 启动参数样例：nettyproxy.port=5432 
	 */
	private Integer port;
	/**
	 * Netty中worker线程数量
	 * 启动参数样例：nettyproxy.workercnt=80
	 */
	private int workerCnt = Runtime.getRuntime().availableProcessors() * 8;
	/**
	 * 墙外服务器地址，只有runmode=11、12时该参数才生效
	 * 启动参数样例：nettyproxy.outside_proxy_host=57.12.39.152
	 */
	private String outsideProxyHost;
	/**
	 * 墙外服务器端口
	 * 启动参数样例：nettyproxy.outside_proxy_port=33121
	 */
	private Integer outsideProxyPort;
	
	public JladderFullConfig() { 
	}
	
	public JladderFullConfig(RunModeEnum runMode, int port) {
		this.runMode = runMode;
		this.port = port;
	}
}
