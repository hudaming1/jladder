package org.jladder.test;

import org.jladder.common.core.ServerRunProxyFactory;
import org.jladder.common.core.config.JladderFullConfig;
import org.jladder.common.enumtype.RunModeEnum;

public class InsideServerStart {

	public static void main(String[] args) {
		JladderFullConfig jladderConfig = new JladderFullConfig(RunModeEnum.HttpInsideServer, 10086);
//		jladderConfig.setOutsideProxyHost("47.75.102.227");
		jladderConfig.setOutsideProxyHost("localhost");
		jladderConfig.setOutsideProxyPort(5432);
		ServerRunProxyFactory.create(jladderConfig).start();
	}
}
