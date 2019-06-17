package org.hum.nettyproxy.adapter.http.simpleserver;

import org.hum.nettyproxy.common.NamedThreadFactory;
import org.hum.nettyproxy.common.codec.http.HttpRequestDecoder;
import org.hum.nettyproxy.common.core.NettyProxyConfig;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyHttpServer implements Runnable {

	// 建议HTTP服务器，线程数1即可
	private static final int SINGLE_THREAD_COUNT = 1;
	private static final Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);
	private final String HttpSimpleServerThreadNamePrefix = "http-simple-server";
	
	private final ServerBootstrap serverBootstrap;
	private final NettyProxyConfig config;

	public NettyHttpServer(NettyProxyConfig config) {
		this.config = config;
		serverBootstrap = new ServerBootstrap();
		NettyProxyContext.regist(config);
	}
	

	@Override
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(SINGLE_THREAD_COUNT, new NamedThreadFactory(HttpSimpleServerThreadNamePrefix + "-boss-thread"));
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(SINGLE_THREAD_COUNT, new NamedThreadFactory(HttpSimpleServerThreadNamePrefix + "-worker-thread"));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.group(bossGroup, workerGroup);
		serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new HttpRequestDecoder());
				ch.pipeline().addLast(new NettySimpleServerHandler());
			}
		});
		serverBootstrap.bind(config.getBindHttpServerPort()).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				logger.info("http-simple-server started, listening port: " + config.getBindHttpServerPort());
			}
		});
	}
}
