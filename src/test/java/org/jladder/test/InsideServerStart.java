package org.jladder.test;

import org.jladder.common.core.ServerRunProxyFactory;
import org.jladder.common.core.config.JladderConfig;
import org.jladder.common.enumtype.RunModeEnum;

public class InsideServerStart {

	public static void main(String[] args) {
		JladderConfig jladderConfig = new JladderConfig(RunModeEnum.HttpInsideServer, 10086);
		jladderConfig.setOutsideProxyHost("47.75.102.227");
		jladderConfig.setOutsideProxyPort(5432);
		ServerRunProxyFactory.create(RunModeEnum.HttpInsideServer).start(jladderConfig);
	}
}
