package org.hum.nettyproxy;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.hum.nettyproxy.common.core.ServerRunProxyFactory;
import org.hum.nettyproxy.common.core.config.NettyProxyConfig;
import org.hum.nettyproxy.common.core.config.proploader.NettyProxyConfigPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRun {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerRun.class);
	
	public static interface Starter {
		void start(NettyProxyConfig args);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		NettyProxyConfig serverRunArg = new NettyProxyConfigPropertiesLoader().load(ServerRun.class.getResource("/nettyproxy_http_simpleproxy.properties").getFile());
		logger.info("input_args=" + serverRunArg);
		ServerRunProxyFactory.create(serverRunArg.getRunMode()).start(serverRunArg);
	}
}
