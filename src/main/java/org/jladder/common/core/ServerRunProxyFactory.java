package org.jladder.common.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jladder.ServerRun.Starter;
import org.jladder.adapter.http.insideproxy.NettyHttpInsideProxyServer;
import org.jladder.adapter.outside.JladderOutsideProxyServer;
import org.jladder.adapter.socks5.NettySocksInsideProxyServer;
import org.jladder.common.NamedThreadFactory;
import org.jladder.common.core.config.JladderConfig;
import org.jladder.common.enumtype.RunModeEnum;
import org.jladder.common.exception.JladderException;

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
	public void start(JladderConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("config mustn't be null");
		}
		
		_start(config);
	}
	
	public abstract void _start(JladderConfig config);
}

class NettyHttpSimpleStarter extends AbstractNettyPorxyStarter {
	
	@Override
	public void _start(JladderConfig config) {
		throw new JladderException("unsupport");
	}
}

class NettyHttpInsideProxyStarter extends AbstractNettyPorxyStarter {
	
	@Override
	public void _start(JladderConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettyHttpInsideProxyServer(config));
	}
}

class OutsideProxyStarter extends AbstractNettyPorxyStarter {
	
	@Override
	public void _start(JladderConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new JladderOutsideProxyServer(config));
	}
}

class NettySocksInsideProxyStarter extends AbstractNettyPorxyStarter {
	
	@Override
	public void _start(JladderConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettySocksInsideProxyServer(config));
	}
}