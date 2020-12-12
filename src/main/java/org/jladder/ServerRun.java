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

	/**
	 * @param args
	 * <pre>
	 *  墙内服务器(http-inside-server)启动命令:
	 *    nettyproxy.runmode=11 nettyproxy.port=51996 nettyproxy.outside_proxy_host=47.75.102.227 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=96
	 *  墙内服务器(socks-inside-server)启动命令:
	 *    nettyproxy.runmode=12 nettyproxy.port=52996 nettyproxy.outside_proxy_host=47.75.102.227 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=96
	 *  墙外服务器(outside-server)启动命令:
	 *    nettyproxy.runmode=100 nettyproxy.port=5432 nettyproxy.workercnt=96
	 *	HTTP转发服务器(http-simple-server)启动命令:
	 *     nettyproxy.runmode=1 nettyproxy.port=3389 nettyproxy.workercnt=96 nettyproxy.http_server_port=80
	 * </pre> 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
//		args = "nettyproxy.runmode=100 nettyproxy.port=5432 nettyproxy.workercnt=96".split(" ");
//		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=11 nettyproxy.port=51996 nettyproxy.outside_proxy_host=47.75.102.227 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=8 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://hudaming.com nettyproxy.intercept-redirect=hudaming.com->39.96.83.46".split(" ");
//		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=12 nettyproxy.port=52996 nettyproxy.outside_proxy_host=47.75.102.227 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=96 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://127.0.0.1:80".split(" ");
//		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=1 nettyproxy.port=51996 nettyproxy.workercnt=96 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://hudaming.com nettyproxy.intercept-redirect=hudaming.com->127.0.0.1".split(" ");
//		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=11 nettyproxy.port=51996 nettyproxy.outside_proxy_host=47.75.102.227 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=8 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://hudaming.com nettyproxy.intercept-redirect=hudaming.com->127.0.0.1 nettyproxy.webroot=/Users/hudaming/Workspace/GitHub/netty-proxy/src/main/resources/webapps".split(" ");
//		args = "nettyproxy.enableauthority=1 nettyproxy.runmode=11 nettyproxy.port=51996 nettyproxy.outside_proxy_host=localhost nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=8 nettyproxy.http_server_port=80 nettyproxy.http_server_url=http://hudaming.com nettyproxy.intercept-redirect=hudaming.com->127.0.0.1".split(" ");
//		args = "nettyproxy.runmode=12 nettyproxy.port=52996 nettyproxy.outside_proxy_host=47.75.102.227:5432 nettyproxy.consoleport=80".split(" ");
		args = "nettyproxy.runmode=11 nettyproxy.port=52007 nettyproxy.outside_proxy_host=47.75.102.227:5432".split(" ");
//		args = "nettyproxy.runmode=1 nettyproxy.port=52007 nettyproxy.interceptor=1 nettyproxy.interceptor.regx=[\"update header.host = 'localhost:8080' where header.host= '129.28.193.172:8080'\", \"update header.host = 'localhost:8080' where header.host= '129.28.193.172:8080'\"]".split("nettyproxy.");	
//		args = "nettyproxy={runmode:1, port:52007}".split(" ");
		JladderConfig serverRunArg = new NettyProxyConfigPropertiesLoader().load(ServerRun.class.getResource("/nettyproxy_http_simpleproxy.properties").getFile());
//		logger.info("input_args=" + serverRunArg);
		serverRunArg.setOutsideProxyHost("47.75.102.227");
		serverRunArg.setOutsideProxyPort(5432);
		serverRunArg.setRunMode(RunModeEnum.HttpInsideServer);
		ServerRunProxyFactory.create(serverRunArg.getRunMode()).start(serverRunArg);
	}
}
