package org.hum.nettyproxy;

import org.hum.nettyproxy.core.NettyProxyConfig;
import org.hum.nettyproxy.core.NettyProxyConfigParser;
import org.hum.nettyproxy.core.ServerRunProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRun {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerRun.class);
	
	public static interface Starter {
		void start(NettyProxyConfig args);
	}

	/**
	 * @param args
	 *  nettyproxy.runmode=11 nettyproxy.port=3389 nettyproxy.outside_proxy_host=127.0.0.1 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=10
	 */
	public static void main(String[] args) {
		args = "nettyproxy.runmode=11 nettyproxy.port=3389 nettyproxy.outside_proxy_host=127.0.0.1 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=10".split(" ");
		NettyProxyConfig serverRunArg = NettyProxyConfigParser.toServerRunArg(args);
		logger.info("input_args=" + serverRunArg);
		ServerRunProxyFactory.create(serverRunArg.getRunMode()).start(serverRunArg);
	}
}
