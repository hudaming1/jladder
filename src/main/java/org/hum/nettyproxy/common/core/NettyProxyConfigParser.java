package org.hum.nettyproxy.common.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hum.nettyproxy.common.enumtype.RunModeEnum;
import org.hum.nettyproxy.common.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyProxyConfigParser {

	private static final Logger logger = LoggerFactory.getLogger(NettyProxyConfigParser.class);
	private static final String NETTY_PROXY_ARGS_PREFIX = "nettyproxy.";
	private static final String RUNMODE_KEY = "runmode";
	private static final String PORT_KEY = "port";
	private static final String HTTP_SERVER_PORT_KEY = "http_server_port";
	private static final String WORKER_CNT_KEY = "workercnt";
	private static final String OUTSIDE_PROXY_HOST_KEY = "outside_proxy_host";
	private static final String OUTSIDE_PROXY_PORT_KEY = "outside_proxy_port";
	
	private static final int DETECTIVE_OUTSIDE_TIMEOUT = 8000; // 探测墙外服务器超时时间
	private static final int DEFAULT_LISTENNING_PORT = 52996; // 默认监听端口
	private static final int DEFAULT_WORKER_CNT = Runtime.getRuntime().availableProcessors() * 10; // 默认workerCount数量
	private static final NettyProxyConfig DEFAULT_SERVER_RUN_ARGS = new NettyProxyConfig(RunModeEnum.HttpSimpleProxy, DEFAULT_LISTENNING_PORT); // 默认参数
	
	public static NettyProxyConfig toServerRunArg(String args[]) {
		logger.debug("prepare parse args, input_args=" + Arrays.toString(args));
		
		Map<String, String> paramMap = toMap(args);
		if (paramMap == null || paramMap.isEmpty()) {
			// 使用默认启动参数
			logger.info("no args found, use default param=" + DEFAULT_SERVER_RUN_ARGS);
			return DEFAULT_SERVER_RUN_ARGS;
		}

		RunModeEnum runMode = RunModeEnum.getEnum(parseInt(paramMap.get(RUNMODE_KEY), "param \"runmode[" + paramMap.get(RUNMODE_KEY) + "]\" is invaild"));
		
		NettyProxyConfig serverRunArgs = new NettyProxyConfig();
		serverRunArgs.setRunMode(runMode);
		serverRunArgs.setPort(parseInt(paramMap.get(PORT_KEY), "param \"port [" + paramMap.get(PORT_KEY) + "]\" is invaild"));
		serverRunArgs.setWorkerCnt(paramMap.containsKey(WORKER_CNT_KEY)? parseInt(paramMap.get(WORKER_CNT_KEY), "param \"workercnt[" + paramMap.get(WORKER_CNT_KEY) + "]\" is invaild") : DEFAULT_WORKER_CNT);
		if (paramMap.containsKey(HTTP_SERVER_PORT_KEY)) {
			serverRunArgs.setBindHttpServerPort(parseInt(paramMap.get(HTTP_SERVER_PORT_KEY), "param \"http_server_port[" + paramMap.get(HTTP_SERVER_PORT_KEY) + "]\" is invaild"));
		}
		
		if (runMode == RunModeEnum.HttpInsideServer || runMode == RunModeEnum.SocksInsideServer) { 
			String outsideProxyHost = paramMap.get(OUTSIDE_PROXY_HOST_KEY);
			if (outsideProxyHost == null || outsideProxyHost.isEmpty()) {
				throw new IllegalArgumentException("param \"outside_proxy_host\" is invaild");
			}
			serverRunArgs.setOutsideProxyHost(outsideProxyHost);
			serverRunArgs.setOutsideProxyPort(parseInt(paramMap.get(OUTSIDE_PROXY_PORT_KEY), "param \"outside_proxy_port[" + paramMap.get(OUTSIDE_PROXY_PORT_KEY) + "]\" is invaild"));
			
			logger.info("now checking outside_proxy[{}{}] is reachable...", serverRunArgs.getOutsideProxyHost(), serverRunArgs.getOutsideProxyPort());
			// 检测Proxy是否可达
			if (!NetUtil.isReachable(serverRunArgs.getOutsideProxyHost(), serverRunArgs.getOutsideProxyPort(), DETECTIVE_OUTSIDE_TIMEOUT)) {
				logger.error("===========================Notice=================================");
				logger.error("\toutside_server[" + serverRunArgs.getOutsideProxyHost() + ":" + serverRunArgs.getOutsideProxyPort() + "] unreachabled");
				logger.error("==================================================================");
			} 
			logger.info("outside_server[{}:{}] is reachable", serverRunArgs.getOutsideProxyHost(), serverRunArgs.getOutsideProxyPort());
		}
		
		return serverRunArgs;
	}
	
	public static int parseInt(String str, String message) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ce) {
			throw new IllegalArgumentException(message, ce);
		}
	}
	
	public static Map<String, String> toMap(String args[]) {
		if (args == null || args.length == 0) {
			return Collections.emptyMap();
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		for (String arg : args) {
			if (arg.startsWith(NETTY_PROXY_ARGS_PREFIX)) {
				arg = arg.replace(NETTY_PROXY_ARGS_PREFIX, ""); // delete prefix
				int splitIndex = arg.indexOf("=");
				if (splitIndex <= 0) {
					logger.warn("found invaild param=" + arg);
					throw new IllegalArgumentException("found invaild param=" + arg);
				}
				String key = arg.substring(0, splitIndex);
				String value = arg.substring(splitIndex + 1, arg.length());
				paramMap.put(key, value);
				logger.info("parse arg success, key={}, value={}", key, value);
			}
		}
		return paramMap;
	}
}