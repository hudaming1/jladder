package org.hum.nettyproxy.common.core;

import java.util.concurrent.ConcurrentHashMap;

import org.hum.nettyproxy.common.enumtype.RunModeEnum;
import org.hum.nettyproxy.compoment.monitor.NettyProxyMonitorManager;

public class NettyProxyContext {
	
	private final static ConcurrentHashMap<String, NettyProxyConfig> CONFIG_MAP = new ConcurrentHashMap<String, NettyProxyConfig>();
	private final static InheritableThreadLocal<NettyProxyConfig> CONFIG_CONTEXT = new InheritableThreadLocal<NettyProxyConfig>();
	private final static InheritableThreadLocal<NettyProxyMonitorManager> MONITOR_CONTEXT = new InheritableThreadLocal<NettyProxyMonitorManager>();

	public static void regist(NettyProxyConfig nettyConfig) {
		if (nettyConfig == null || nettyConfig.getRunMode() == null) {
			throw new IllegalArgumentException("param mustn't be null");
		}
		CONFIG_MAP.put(nettyConfig.getRunMode().getName(), nettyConfig);
		
		CONFIG_CONTEXT.set(nettyConfig);
	}
	
	public static void regist(NettyProxyMonitorManager monitor) {
		MONITOR_CONTEXT.set(monitor);
	}
	
	public static void regist(NettyProxyConfig nettyConfig, NettyProxyMonitorManager monitor) {
		regist(monitor);
		regist(nettyConfig);
	}

	public static NettyProxyConfig getConfigByThreadName() {
		String currentThreadName = Thread.currentThread().getName();
		for (RunModeEnum runMode : RunModeEnum.values()) {
			if (currentThreadName.startsWith(runMode.getName())) {
				return CONFIG_MAP.get(runMode.getName());
			}
		}
		return null;
	}

	public static NettyProxyConfig getConfig() {
		return CONFIG_CONTEXT.get();
	}
	
	public static NettyProxyMonitorManager getMonitor() {
		return MONITOR_CONTEXT.get();
	}
	
}
