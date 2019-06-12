package org.hum.nettyproxy.core;

import org.hum.nettyproxy.ServerRun.Starter;
import org.hum.nettyproxy.adapter.http.NettyHttpInsideProxyServer;
import org.hum.nettyproxy.common.enumtype.RunModeEnum;

public class ServerRunProxyFactory {
	
	private static final NettyHttpSimpleStarter nettyHttpSimpleStarter = new NettyHttpSimpleStarter();
	private static final NettyHttpInsideProxyStarter nettyHttpInsideProxyStarter = new NettyHttpInsideProxyStarter();

	public static Starter create(RunModeEnum runMode) {
		if (runMode == RunModeEnum.HttpSimpleProxy) {
			return nettyHttpSimpleStarter;
		} else if (runMode == RunModeEnum.HttpInsideServer) {
			return nettyHttpInsideProxyStarter;
		}
		
		throw new IllegalArgumentException("unimplement run_mode=" + runMode.getCode());
	}
}

class NettyHttpSimpleStarter implements Starter {
	
	@Override
	public void start(NettyProxyConfig args) {
		// TODO
	}
}


class NettyHttpInsideProxyStarter implements Starter {
	
	@Override
	public void start(NettyProxyConfig args) {
		new Thread(new NettyHttpInsideProxyServer(args)).start();
	}
}
