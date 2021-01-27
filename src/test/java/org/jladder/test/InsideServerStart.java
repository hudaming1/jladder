package org.jladder.test;

import org.jladder.common.enumtype.RunModeEnum;
import org.jladder.config.JladderFullConfig;
import org.jladder.config.ServerRunProxyFactory;

public class InsideServerStart {

	public static void main(String[] args) {
		JladderFullConfig jladderConfig = new JladderFullConfig(RunModeEnum.HttpInsideServer, 52007);
//		jladderConfig.setOutsideProxyHost("47.75.102.227");
		jladderConfig.setOutsideProxyHost("localhost");
		jladderConfig.setOutsideProxyPort(5432);
		ServerRunProxyFactory.create(jladderConfig).start();
	}
}
