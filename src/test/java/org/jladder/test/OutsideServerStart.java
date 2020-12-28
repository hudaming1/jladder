package org.jladder.test;

import org.jladder.common.enumtype.RunModeEnum;
import org.jladder.config.JladderFullConfig;
import org.jladder.config.ServerRunProxyFactory;

public class OutsideServerStart {

	public static void main(String[] args) {
		ServerRunProxyFactory.create(new JladderFullConfig(RunModeEnum.OutsideServer, 5432)).start();
	}
}
