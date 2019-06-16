package org.hum.nettyproxy.server;

import org.hum.nettyproxy.common.NamedThreadFactory;
import org.hum.nettyproxy.common.codec.customer.NettyProxyConnectMessageCodec;
import org.hum.nettyproxy.common.enumtype.RunModeEnum;
import org.hum.nettyproxy.common.util.NettyBootstrapUtil;
import org.hum.nettyproxy.compoment.monitor.NettyProxyMonitorManager;
import org.hum.nettyproxy.compoment.monitor.NettyProxyMonitorHandler;
import org.hum.nettyproxy.core.NettyProxyConfig;
import org.hum.nettyproxy.core.NettyProxyContext;
import org.hum.nettyproxy.server.handler.NettyServerPipeChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyOutsideProxyServer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(NettyOutsideProxyServer.class);
	private final String OutSideServerThreadNamePrefix = RunModeEnum.OutsideServer.getName();
	
	private final ServerBootstrap serverBootstrap;
	private final HttpChannelInitializer httpChannelInitializer;
	private final NettyProxyMonitorManager nettyProxyMonitorManager;
	private final NettyProxyConfig config;

	public NettyOutsideProxyServer(NettyProxyConfig config) {
		this.config = config;
		serverBootstrap = new ServerBootstrap();
		httpChannelInitializer = new HttpChannelInitializer();
		nettyProxyMonitorManager = new NettyProxyMonitorManager();
		NettyProxyContext.regist(config, nettyProxyMonitorManager);
	}
	
	@Override
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, new NamedThreadFactory(OutSideServerThreadNamePrefix + "-boss-thread"));
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(config.getWorkerCnt(), new NamedThreadFactory(OutSideServerThreadNamePrefix + "-worker-thread"));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.group(bossGroup, workerGroup);
		serverBootstrap.childHandler(httpChannelInitializer);
		
		// 配置TCP参数
		NettyBootstrapUtil.initTcpServerOptions(serverBootstrap, config);
		
		serverBootstrap.bind(config.getPort()).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				logger.info("outside-server started, listening port: " + config.getPort());
			}
		});
	}
	
	private static class HttpChannelInitializer extends ChannelInitializer<Channel> {
		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addFirst(new NettyProxyMonitorHandler());
			ch.pipeline().addLast(new NettyProxyConnectMessageCodec.Decoder(), new NettyServerPipeChannelHandler());
		}
	}
}