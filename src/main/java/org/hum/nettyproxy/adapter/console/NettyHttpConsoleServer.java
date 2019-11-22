package org.hum.nettyproxy.adapter.console;

import org.hum.nettyproxy.adapter.console.handler.HttpAuthorityLoginHandler;
import org.hum.nettyproxy.common.NamedThreadFactory;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.core.config.NettyProxyConfig;
import org.hum.nettyproxy.compoment.auth.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyHttpConsoleServer implements Runnable {

	// 建议HTTP服务器，线程数1即可
	private static final int SINGLE_THREAD_COUNT = 1;
	private static final Logger logger = LoggerFactory.getLogger(NettyHttpConsoleServer.class);
	private final String HttpConsoleServerThreadNamePrefix = "http-console-server";
	
	private final ServerBootstrap serverBootstrap;
	private final NettyProxyConfig config;

	public NettyHttpConsoleServer(NettyProxyConfig config) {
		this.config = config;
		serverBootstrap = new ServerBootstrap();
		NettyProxyContext.regist(config);
	}
	
	@Override
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(SINGLE_THREAD_COUNT, new NamedThreadFactory(HttpConsoleServerThreadNamePrefix + "-boss-thread"));
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(SINGLE_THREAD_COUNT, new NamedThreadFactory(HttpConsoleServerThreadNamePrefix + "-worker-thread"));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.group(bossGroup, workerGroup);
		serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
			private final NettyConsoleServerHandler nettyConsoleServerHandler = new NettyConsoleServerHandler();
			private Boolean isEnableAuthority = NettyProxyContext.getConfig().getEnableAuthority();
			private HttpAuthorityLoginHandler authorityHandler = new HttpAuthorityLoginHandler(AuthManager.getInstance());
			
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new HttpServerCodec());
				ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
				if (isEnableAuthority != null && isEnableAuthority == true) {
					ch.pipeline().addLast(authorityHandler);
				}
				ch.pipeline().addLast(nettyConsoleServerHandler);
			}
		});
		serverBootstrap.bind(config.getConsolePort()).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				logger.info("http-console-server started, listening port: " + config.getBindHttpServerPort());
			}
		});
	}
}
