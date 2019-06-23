package org.hum.nettyproxy.common.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hum.nettyproxy.ServerRun.Starter;
import org.hum.nettyproxy.adapter.http.consoleserver.NettyHttpConsoleServer;
import org.hum.nettyproxy.adapter.http.insideproxy.NettyHttpInsideProxyServer;
import org.hum.nettyproxy.adapter.http.simpleproxy.NettyHttpSimpleProxyServer;
import org.hum.nettyproxy.adapter.socks5.NettySocksInsideProxyServer;
import org.hum.nettyproxy.common.NamedThreadFactory;
import org.hum.nettyproxy.common.enumtype.RunModeEnum;
import org.hum.nettyproxy.server.NettyOutsideProxyServer;

public class ServerRunProxyFactory {
	
	private static final NettyHttpSimpleStarter httpSimpleStarter = new NettyHttpSimpleStarter();
	private static final NettyHttpInsideProxyStarter httpInsideProxyStarter = new NettyHttpInsideProxyStarter();
	private static final OutsideProxyStarter outsideProxyStarter = new OutsideProxyStarter();
	private static final NettySocksInsideProxyStarter socksInsideProxyStarter = new NettySocksInsideProxyStarter();
	static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2, new NamedThreadFactory("netty-proxy-starter"));

	public static Starter create(RunModeEnum runMode) {
		if (runMode == RunModeEnum.HttpSimpleProxy) {
			return httpSimpleStarter;
		} else if (runMode == RunModeEnum.HttpInsideServer) {
			return httpInsideProxyStarter;
		} else if (runMode == RunModeEnum.OutsideServer) {
			return outsideProxyStarter;
		} else if (runMode == RunModeEnum.SocksInsideServer) {
			return socksInsideProxyStarter;
		}
		
		throw new IllegalArgumentException("unimplement run_mode=" + runMode.getCode());
	}
}

abstract class AbstractNettyPorxyStarter implements Starter {

	@Override
	public void start(NettyProxyConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("config mustn't be null");
		}
		
		if (config.getBindHttpServerPort() != null && config.getBindHttpServerPort() > 0) {
			ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettyHttpConsoleServer(config));
		}
		
		_start(config);
	}
	
	public abstract void _start(NettyProxyConfig config);
}

class NettyHttpSimpleStarter extends AbstractNettyPorxyStarter {
	
	@Override
	public void _start(NettyProxyConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettyHttpSimpleProxyServer(config));
	}
}

class NettyHttpInsideProxyStarter extends AbstractNettyPorxyStarter {
	
	@Override
	public void _start(NettyProxyConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettyHttpInsideProxyServer(config));
	}
}

class OutsideProxyStarter extends AbstractNettyPorxyStarter {
	
	@Override
	public void _start(NettyProxyConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettyOutsideProxyServer(config));
	}
}

class NettySocksInsideProxyStarter extends AbstractNettyPorxyStarter {
	
	@Override
	public void _start(NettyProxyConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettySocksInsideProxyServer(config));
	}
}