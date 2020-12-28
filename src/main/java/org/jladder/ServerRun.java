package org.jladder;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jladder.common.core.ServerRunProxyFactory;
import org.jladder.common.core.config.impl.HttpInsideJladderConfig;

public class ServerRun {
	
//	本地启动HTTP代理
	public static void main(String[] args) throws FileNotFoundException, IOException {
		HttpInsideJladderConfig serverRunArg = new HttpInsideJladderConfig();
		serverRunArg.port(52007).workerCnt(Runtime.getRuntime().availableProcessors()).outsideProxyHost("47.75.102.227").outsideProxyPort(5432);
		ServerRunProxyFactory.create(serverRunArg.build()).start();
	}

//	本地启动Socks代理
//	public static void main(String[] args) throws FileNotFoundException, IOException {
//		JladderFullConfig serverRunArg = new JladderFullConfig();
//		serverRunArg.setPort(52007);
//		serverRunArg.setWorkerCnt(Runtime.getRuntime().availableProcessors());
//		serverRunArg.setOutsideProxyHost("47.75.102.227");
//		serverRunArg.setOutsideProxyPort(5432);
//		serverRunArg.setRunMode(RunModeEnum.SocksInsideServer);
//		ServerRunProxyFactory.create(serverRunArg.getRunMode()).start(serverRunArg);
//	}
	
//	这是我在47.75.102.227启动参数
//	public static void main(String[] args) throws FileNotFoundException, IOException {
//		ServerRunProxyFactory.create(RunModeEnum.OutsideServer).start(new JladderFullConfig(RunModeEnum.OutsideServer, 5432));
//	}
}
