package org.jladder.common.core;

import java.util.concurrent.ConcurrentHashMap;

import org.jladder.common.core.config.JladderFullConfig;
import org.jladder.common.enumtype.RunModeEnum;

public class JladderContext {
	
	private final static ConcurrentHashMap<String, JladderFullConfig> CONFIG_MAP = new ConcurrentHashMap<String, JladderFullConfig>();
	private final static InheritableThreadLocal<JladderFullConfig> CONFIG_CONTEXT = new InheritableThreadLocal<JladderFullConfig>();

	public static void regist(JladderFullConfig nettyConfig) {
		if (nettyConfig == null || nettyConfig.getRunMode() == null) {
			throw new IllegalArgumentException("param mustn't be null");
		}
		CONFIG_MAP.put(nettyConfig.getRunMode().getName(), nettyConfig);
		
		CONFIG_CONTEXT.set(nettyConfig);
	}
	
	public static JladderFullConfig getConfigByThreadName() {
		String currentThreadName = Thread.currentThread().getName();
		for (RunModeEnum runMode : RunModeEnum.values()) {
			if (currentThreadName.startsWith(runMode.getName())) {
				return CONFIG_MAP.get(runMode.getName());
			}
		}
		return null;
	}

	public static JladderFullConfig getConfig() {
		return CONFIG_CONTEXT.get();
	}
	
}
