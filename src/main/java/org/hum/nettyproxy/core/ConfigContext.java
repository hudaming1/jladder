package org.hum.nettyproxy.core;

import java.util.concurrent.ConcurrentHashMap;

import org.hum.nettyproxy.common.enumtype.RunModeEnum;

public class ConfigContext {
	
	private final static ConcurrentHashMap<String, NettyProxyConfig> CONFIG_MAP = new ConcurrentHashMap<String, NettyProxyConfig>();

	public static void regist(NettyProxyConfig nettyConfig) {
		if (nettyConfig == null || nettyConfig.getRunMode() == null) {
			throw new IllegalArgumentException("param mustn't be null");
		}
		CONFIG_MAP.put(nettyConfig.getRunMode().getName(), nettyConfig);
	}

	public static NettyProxyConfig getConfig() {
		String currentThreadName = Thread.currentThread().getName();
		for (RunModeEnum runMode : RunModeEnum.values()) {
			if (currentThreadName.startsWith(runMode.getName())) {
				return CONFIG_MAP.get(runMode.getName());
			}
		}
		return null;
	}
}
