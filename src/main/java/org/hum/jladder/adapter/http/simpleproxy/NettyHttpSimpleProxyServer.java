package org.hum.jladder.adapter.http.simpleproxy;

import org.hum.jladder.common.NamedThreadFactory;
import org.hum.jladder.common.codec.http.HttpRequestDecoder;
import org.hum.jladder.common.core.NettyProxyContext;
import org.hum.jladder.common.core.config.NettyProxyConfig;
import org.hum.jladder.common.enumtype.RunModeEnum;
import org.hum.jladder.common.util.NettyBootstrapUtil;
import org.hum.jladder.compoment.monitor.NettyProxyMonitorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * SimpleProxy -> 单服务器翻墙，只将sourceServer请求forward到targetServer
 * @author hudaming
 */
public class NettyHttpSimpleProxyServer implements Runnable  {

	private static final Logger logger = LoggerFactory.getLogger(NettyHttpSimpleProxyServer.class);
	private final String HttpSimpleServerThreadNamePrefix = RunModeEnum.HttpSimpleProxy.getName();
	
	private final ServerBootstrap serverBootstrap;
	private final HttpChannelInitializer httpChannelInitializer;
	private final NettyProxyConfig config;

	public NettyHttpSimpleProxyServer(NettyProxyConfig config) {
		this.config = config;
		NettyProxyContext.regist(config);
		serverBootstrap = new ServerBootstrap();
		httpChannelInitializer = new HttpChannelInitializer();
	}
	
	@Override
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, new NamedThreadFactory(HttpSimpleServerThreadNamePrefix + "-boss-thread"));
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(config.getWorkerCnt(), new NamedThreadFactory(HttpSimpleServerThreadNamePrefix + "-worker-thread"));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.group(bossGroup, workerGroup);
		serverBootstrap.childHandler(httpChannelInitializer);
		
		// 配置TCP参数
		NettyBootstrapUtil.initTcpServerOptions(serverBootstrap, config);
		
		serverBootstrap.bind(config.getPort()).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				logger.info("http-simple-proxy-server started, listening port: " + config.getPort());
			}
		});
	}
	
	private static class HttpChannelInitializer extends ChannelInitializer<Channel> {

		private final NettyProxyMonitorHandler nettyProxyMonitorHandler = new NettyProxyMonitorHandler();
		private HttpProxyProcessHandler httpProxyProcessHandler = new HttpProxyProcessHandler();
		
		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addFirst(nettyProxyMonitorHandler);
			ch.pipeline().addLast(new HttpRequestDecoder());
			ch.pipeline().addLast(httpProxyProcessHandler);
		}
	}
}
