package org.jladder.config.impl;

import org.jladder.common.enumtype.RunModeEnum;
import org.jladder.config.JladderConfigBuilder;
import org.jladder.config.JladderFullConfig;

public class SocksInsideJladderConfig implements JladderConfigBuilder {

	/**
	 * 服务启动监听端口，代理程序需要将流量转发到这个端口方可实现转发
	 */
	private Integer port;
	/**
	 * Netty中worker线程数量
	 */
	private int workerCnt;
	/**
	 * 墙外服务器地址
	 */
	private String outsideProxyHost;
	/**
	 * 墙外服务器端口
	 */
	private Integer outsideProxyPort;
	
	public SocksInsideJladderConfig port(int port) {
		this.port = port;
		return this;
	}
	
	public SocksInsideJladderConfig workerCnt(int workerCnt) {
		this.workerCnt = workerCnt;
		return this;
	}
	
	public SocksInsideJladderConfig outsideProxyHost(String outsideProxyHost) {
		this.outsideProxyHost = outsideProxyHost;
		return this;
	}
	
	public SocksInsideJladderConfig outsideProxyPort(int outsideProxyPort) {
		this.outsideProxyPort = outsideProxyPort;
		return this;
	}
	
	@Override
	public JladderFullConfig build() {
		JladderFullConfig jladderConfig = new JladderFullConfig();
		jladderConfig.setRunMode(RunModeEnum.SocksInsideServer);
		jladderConfig.setPort(port);
		jladderConfig.setOutsideProxyHost(outsideProxyHost);
		jladderConfig.setOutsideProxyPort(outsideProxyPort);
		jladderConfig.setWorkerCnt(workerCnt);
		return jladderConfig;
	}
}
