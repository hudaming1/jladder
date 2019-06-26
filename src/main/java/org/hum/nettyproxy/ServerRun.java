package org.hum.nettyproxy;

import org.hum.nettyproxy.common.core.NettyProxyConfig;
import org.hum.nettyproxy.common.core.NettyProxyConfigParser;
import org.hum.nettyproxy.common.core.ServerRunProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRun {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerRun.class);
	
	public static interface Starter {
		void start(NettyProxyConfig args);
	}

	/**
	 * @param args
	 * <pre>
	 *  墙内服务器(http-inside-server)启动命令:
	 *    nettyproxy.runmode=11 nettyproxy.port=51996 nettyproxy.outside_proxy_host=localhost nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=96
	 *  墙内服务器(socks-inside-server)启动命令:
	 *    nettyproxy.runmode=12 nettyproxy.port=52996 nettyproxy.outside_proxy_host=localhost nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=96
	 *  墙外服务器(outside-server)启动命令:
	 *    nettyproxy.runmode=100 nettyproxy.port=5432 nettyproxy.workercnt=96
	 *	HTTP转发服务器(http-simple-server)启动命令:
	 *     nettyproxy.runmode=1 nettyproxy.port=3389 nettyproxy.workercnt=96 nettyproxy.http_server_port=80
	 * </pre> 
	 */
	public static void main(String[] args) {
		args = "nettyproxy.runmode=100 nettyproxy.port=5432 nettyproxy.workercnt=96".split(" ");
//		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=11 nettyproxy.port=51996 nettyproxy.outside_proxy_host=47.75.102.227 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=8 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://hudaming.com nettyproxy.intercept-redirect=hudaming.com->39.96.83.46".split(" ");
//		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=12 nettyproxy.port=52996 nettyproxy.outside_proxy_host=47.75.102.227 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=96 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://127.0.0.1:80".split(" ");
//		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=1 nettyproxy.port=51996 nettyproxy.workercnt=96 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://hudaming.com nettyproxy.intercept-redirect=hudaming.com->127.0.0.1".split(" ");
		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=11 nettyproxy.port=51996 nettyproxy.outside_proxy_host=localhost nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=8 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://hudaming.com nettyproxy.intercept-redirect=hudaming.com->127.0.0.1".split(" ");
		NettyProxyConfig serverRunArg = NettyProxyConfigParser.toServerRunArg(args);
		logger.info("input_args=" + serverRunArg);
		ServerRunProxyFactory.create(serverRunArg.getRunMode()).start(serverRunArg);
	}
}
