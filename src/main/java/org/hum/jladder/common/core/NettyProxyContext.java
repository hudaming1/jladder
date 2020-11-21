package org.hum.jladder.common.core;

import java.util.concurrent.ConcurrentHashMap;

import org.hum.jladder.common.core.config.JladderConfig;
import org.hum.jladder.common.enumtype.RunModeEnum;
import org.hum.jladder.compoment.monitor.NettyProxyMonitorManager;

public class NettyProxyContext {
	
	private final static ConcurrentHashMap<String, JladderConfig> CONFIG_MAP = new ConcurrentHashMap<String, JladderConfig>();
	private final static InheritableThreadLocal<JladderConfig> CONFIG_CONTEXT = new InheritableThreadLocal<JladderConfig>();
	private final static InheritableThreadLocal<NettyProxyMonitorManager> MONITOR_CONTEXT = new InheritableThreadLocal<NettyProxyMonitorManager>();

	public static void regist(JladderConfig nettyConfig) {
		if (nettyConfig == null || nettyConfig.getRunMode() == null) {
			throw new IllegalArgumentException("param mustn't be null");
		}
		CONFIG_MAP.put(nettyConfig.getRunMode().getName(), nettyConfig);
		
		CONFIG_CONTEXT.set(nettyConfig);
	}
	
	public static void regist(NettyProxyMonitorManager monitor) {
		MONITOR_CONTEXT.set(monitor);
	}
	
	public static void regist(JladderConfig nettyConfig, NettyProxyMonitorManager monitor) {
		regist(monitor);
		regist(nettyConfig);
	}

	public static JladderConfig getConfigByThreadName() {
		String currentThreadName = Thread.currentThread().getName();
		for (RunModeEnum runMode : RunModeEnum.values()) {
			if (currentThreadName.startsWith(runMode.getName())) {
				return CONFIG_MAP.get(runMode.getName());
			}
		}
		return null;
	}

	public static JladderConfig getConfig() {
		return CONFIG_CONTEXT.get();
	}
	
	public static NettyProxyMonitorManager getMonitor() {
		return MONITOR_CONTEXT.get();
	}
	
}
