package org.jladder.config.impl;

import org.jladder.common.enumtype.RunModeEnum;
import org.jladder.config.JladderConfigBuilder;
import org.jladder.config.JladderFullConfig;

public class OutsideJladderConfig implements JladderConfigBuilder {

	/**
	 * 服务启动监听端口，代理程序需要将流量转发到这个端口方可实现转发
	 */
	private Integer port;
	/**
	 * Netty中worker线程数量
	 */
	private int workerCnt;

	public OutsideJladderConfig port(int port) {
		this.port = port;
		return this;
	}
	
	public OutsideJladderConfig workerCnt(int workerCnt) {
		this.workerCnt = workerCnt;
		return this;
	}
	
	@Override
	public JladderFullConfig build() {
		JladderFullConfig jladderConfig = new JladderFullConfig();
		jladderConfig.setRunMode(RunModeEnum.OutsideServer);
		jladderConfig.setPort(port);
		jladderConfig.setWorkerCnt(workerCnt);
		return jladderConfig;
	}
}
