package org.jladder;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jladder.common.core.ServerRunProxyFactory;
import org.jladder.common.core.config.JladderConfig;
import org.jladder.common.core.config.proploader.NettyProxyConfigPropertiesLoader;
import org.jladder.common.enumtype.RunModeEnum;

public class ServerRun {
	
	public static interface Starter {
		void start(JladderConfig args);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		JladderConfig serverRunArg = new NettyProxyConfigPropertiesLoader().load(ServerRun.class.getResource("/nettyproxy_http_simpleproxy.properties").getFile());
		serverRunArg.setOutsideProxyHost("47.75.102.227");
		serverRunArg.setOutsideProxyPort(5432);
		serverRunArg.setRunMode(RunModeEnum.HttpInsideServer);
		ServerRunProxyFactory.create(serverRunArg.getRunMode()).start(serverRunArg);
	}
	
//	这是我在47.75.102.227启动参数
//	public static void main(String[] args) throws FileNotFoundException, IOException {
//		ServerRunProxyFactory.create(RunModeEnum.OutsideServer).start(new JladderConfig(RunModeEnum.OutsideServer, 5432));
//	}
}
