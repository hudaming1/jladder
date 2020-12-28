package org.jladder.common.core;

import java.util.concurrent.ConcurrentHashMap;

import org.jladder.common.core.config.JladderConfig;
import org.jladder.common.enumtype.RunModeEnum;

public class JladderContext {
	
	private final static ConcurrentHashMap<String, JladderConfig> CONFIG_MAP = new ConcurrentHashMap<String, JladderConfig>();
	private final static InheritableThreadLocal<JladderConfig> CONFIG_CONTEXT = new InheritableThreadLocal<JladderConfig>();

	public static void regist(JladderConfig nettyConfig) {
		if (nettyConfig == null || nettyConfig.getRunMode() == null) {
			throw new IllegalArgumentException("param mustn't be null");
		}
		CONFIG_MAP.put(nettyConfig.getRunMode().getName(), nettyConfig);
		
		CONFIG_CONTEXT.set(nettyConfig);
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
	
}
