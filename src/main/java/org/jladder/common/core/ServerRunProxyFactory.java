package org.jladder.common.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jladder.adapter.http.insideproxy.NettyHttpInsideProxyServer;
import org.jladder.adapter.outside.JladderOutsideProxyServer;
import org.jladder.adapter.socks5.NettySocksInsideProxyServer;
import org.jladder.common.NamedThreadFactory;
import org.jladder.common.core.config.JladderFullConfig;
import org.jladder.common.core.config.JladderStarter;
import org.jladder.common.enumtype.RunModeEnum;
import org.jladder.common.exception.JladderException;

public class ServerRunProxyFactory {
	
	static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2, new NamedThreadFactory("netty-proxy-starter"));

	public static JladderStarter create(JladderFullConfig jladderConfig) {
		if (jladderConfig.getRunMode() == RunModeEnum.HttpSimpleProxy) {
			return new NettyHttpSimpleStarter(jladderConfig);
		} else if (jladderConfig.getRunMode() == RunModeEnum.HttpInsideServer) {
			return new NettyHttpInsideProxyStarter(jladderConfig);
		} else if (jladderConfig.getRunMode() == RunModeEnum.OutsideServer) {
			return new OutsideProxyStarter(jladderConfig);
		} else if (jladderConfig.getRunMode() == RunModeEnum.SocksInsideServer) {
			return new NettySocksInsideProxyStarter(jladderConfig);
		}
		
		throw new IllegalArgumentException("unimplement run_mode=" + jladderConfig.getRunMode().getCode());
	}
}

abstract class AbstractJladderStarter implements JladderStarter {
	
	protected JladderFullConfig jladderConfig;
	
	public AbstractJladderStarter(JladderFullConfig jladderConfig) {
		this.jladderConfig = jladderConfig;
	}

	@Override
	public void start() {
		if (jladderConfig == null) {
			throw new IllegalArgumentException("config mustn't be null");
		}
		
		_start(jladderConfig);
	}
	
	public abstract void _start(JladderFullConfig config);
}

class NettyHttpSimpleStarter extends AbstractJladderStarter {
	
	public NettyHttpSimpleStarter(JladderFullConfig jladderConfig) {
		super(jladderConfig);
	}

	@Override
	public void _start(JladderFullConfig config) {
		throw new JladderException("unsupport");
	}
}

class NettyHttpInsideProxyStarter extends AbstractJladderStarter {
	
	public NettyHttpInsideProxyStarter(JladderFullConfig jladderConfig) {
		super(jladderConfig);
	}

	@Override
	public void _start(JladderFullConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettyHttpInsideProxyServer(config));
	}
}

class OutsideProxyStarter extends AbstractJladderStarter {
	
	public OutsideProxyStarter(JladderFullConfig jladderConfig) {
		super(jladderConfig);
	}

	@Override
	public void _start(JladderFullConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new JladderOutsideProxyServer(config));
	}
}

class NettySocksInsideProxyStarter extends AbstractJladderStarter {
	
	public NettySocksInsideProxyStarter(JladderFullConfig jladderConfig) {
		super(jladderConfig);
	}

	@Override
	public void _start(JladderFullConfig config) {
		ServerRunProxyFactory.EXECUTOR_SERVICE.execute(new NettySocksInsideProxyServer(config));
	}
}