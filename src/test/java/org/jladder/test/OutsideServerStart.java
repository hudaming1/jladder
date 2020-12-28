package org.jladder.test;

import org.jladder.common.core.ServerRunProxyFactory;
import org.jladder.common.core.config.JladderFullConfig;
import org.jladder.common.enumtype.RunModeEnum;

public class OutsideServerStart {

	public static void main(String[] args) {
		ServerRunProxyFactory.create(new JladderFullConfig(RunModeEnum.OutsideServer, 5432)).start();
	}
}
