package org.hum.nettyproxy.adapter.http;

import org.hum.nettyproxy.adapter.http.codec.HttpRequestDecoder;
import org.hum.nettyproxy.adapter.http.handler.HttpProxyEncryptHandler;
import org.hum.nettyproxy.common.NamedThreadFactory;
import org.hum.nettyproxy.common.enumtype.RunModeEnum;
import org.hum.nettyproxy.common.util.ServerBootstrapUtil;
import org.hum.nettyproxy.core.ConfigContext;
import org.hum.nettyproxy.core.NettyProxyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyHttpInsideProxyServer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(NettyHttpInsideProxyServer.class);
	private final String HttpInsideServerThreadNamePrefix = RunModeEnum.HttpInsideServer.getName();
	
	private final ServerBootstrap serverBootstrap;
	private final HttpChannelInitializer httpChannelInitializer;
	private final NettyProxyConfig config;

	public NettyHttpInsideProxyServer(NettyProxyConfig config) {
		this.config = config;
		serverBootstrap = new ServerBootstrap();
		httpChannelInitializer = new HttpChannelInitializer();
		ConfigContext.regist(config);
	}
	
	@Override
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, new NamedThreadFactory(HttpInsideServerThreadNamePrefix + "-boss-thread"));
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(config.getWorkerCnt(), new NamedThreadFactory(HttpInsideServerThreadNamePrefix + "-worker-thread"));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.group(bossGroup, workerGroup);
		serverBootstrap.childHandler(httpChannelInitializer);
		
		// 配置TCP参数
		ServerBootstrapUtil.initTcpServerOptions(serverBootstrap, config);
		
		serverBootstrap.bind(config.getPort()).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				logger.info("http-inside-server started, listening port: " + config.getPort());
			}
		});
	}
	
	private static class HttpChannelInitializer extends ChannelInitializer<Channel> {
		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addLast(new HttpRequestDecoder()).addLast(new HttpProxyEncryptHandler());
		}
	}

}
