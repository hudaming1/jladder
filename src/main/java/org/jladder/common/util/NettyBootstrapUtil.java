package org.jladder.common.util;

import org.jladder.common.core.config.JladderFullConfig;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;

public class NettyBootstrapUtil {

	public static void initTcpServerOptions(ServerBootstrap serverBootstrap, JladderFullConfig config) {
		// init server-options
//		if (config.getTcpServerOptions() != null && !config.getTcpServerOptions().isEmpty()) {
//			for (Entry<String, String> tcpOption : config.getTcpServerOptions().entrySet()) {
//				ChannelOption<Object> channelOption = ChannelOption.valueOf(tcpOption.getKey());
//				if (channelOption == null) {
//					logger.warn("unkonwn tcp-server-option=" + tcpOption.getKey());
//					continue;
//				}
//				serverBootstrap.option(channelOption, tcpOption.getValue());
//				logger.info("add tcp-server-option, {}={}", tcpOption.getKey(), tcpOption.getValue());
//			}
//		} else {
//			logger.debug("no tcp-server-options found.");
//			return ;
//		}
//		
//		// init child-options
//		if (config.getTcpServerChildOptions() == null && !config.getTcpServerChildOptions().isEmpty()) {
//			for (Entry<String, String> tcpOption : config.getTcpServerChildOptions().entrySet()) {
//				ChannelOption<Object> channelOption = ChannelOption.valueOf(tcpOption.getKey());
//				if (channelOption == null) {
//					logger.warn("unkonwn tcp-server-option=" + tcpOption.getKey());
//					continue;
//				}
//				serverBootstrap.childOption(channelOption, tcpOption.getValue());
//				logger.info("add tcp-server-child-option, {}={}", tcpOption.getKey(), tcpOption.getValue());
//			}
//		} else {
//			logger.debug("no tcp-server-child-options found.");
//			return ;
//		}
	}

	public static void initTcpServerOptions(Bootstrap bootstrap, JladderFullConfig config) {
		// init server-options
//		if (config.getTcpServerOptions() != null && !config.getTcpServerOptions().isEmpty()) {
//			for (Entry<String, String> tcpOption : config.getTcpServerOptions().entrySet()) {
//				ChannelOption<Object> channelOption = ChannelOption.valueOf(tcpOption.getKey());
//				if (channelOption == null) {
//					logger.warn("unkonwn tcp-option=" + tcpOption.getKey());
//					continue;
//				}
//				bootstrap.option(channelOption, tcpOption.getValue());
//				logger.info("add tcp-option, {}={}", tcpOption.getKey(), tcpOption.getValue());
//			}
//		} else {
//			logger.debug("no tcp-options found.");
//			return ;
//		}
	}
}
