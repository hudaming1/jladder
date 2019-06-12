package org.hum.nettyproxy.core;

import java.util.concurrent.ConcurrentHashMap;

import org.hum.nettyproxy.common.enumtype.RunModeEnum;

public class ConfigContext {
	
	private final static ConcurrentHashMap<String, NettyProxyConfig> CONFIG_MAP = new ConcurrentHashMap<String, NettyProxyConfig>();
	private final static InheritableThreadLocal<NettyProxyConfig> CONFIG_CONTEXT = new InheritableThreadLocal<NettyProxyConfig>();

	public static void regist(NettyProxyConfig nettyConfig) {
		if (nettyConfig == null || nettyConfig.getRunMode() == null) {
			throw new IllegalArgumentException("param mustn't be null");
		}
		CONFIG_MAP.put(nettyConfig.getRunMode().getName(), nettyConfig);
		
		CONFIG_CONTEXT.set(nettyConfig);
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
}
