package org.hum.nettyproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRun {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerRun.class);
	
	public static interface Starter {
		void start(ServerRunArg args);
	}

	/**
	 * @param args
	 *  nettyproxy.runmode=11 nettyproxy.port=5432 nettyproxy.outside_proxy_host=57.12.39.152 nettyproxy.outside_proxy_port=5432 nettyproxy.workercnt=10
	 */
	public static void main(String[] args) {
		logger.info("input_args=" + ServerRunArgParser.toServerRunArg(args));
	}
}
