package org.jladder.test;

import org.jladder.common.core.ServerRunProxyFactory;
import org.jladder.common.core.config.JladderConfig;
import org.jladder.common.enumtype.RunModeEnum;

public class OutsideServerStart {

	public static void main(String[] args) {
		ServerRunProxyFactory.create(RunModeEnum.OutsideServer).start(new JladderConfig(RunModeEnum.OutsideServer, 5432));
	}
}
