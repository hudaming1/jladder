package org.hum.nettyproxy.adapter.http;

import java.util.HashMap;
import java.util.Map;

import org.hum.nettyproxy.adapter.http.handler.HttpProxyProcessHandler;
import org.hum.nettyproxy.common.NamedThreadFactory;
import org.hum.nettyproxy.common.codec.http.HttpRequestDecoder;
import org.hum.nettyproxy.common.enumtype.RunModeEnum;
import org.hum.nettyproxy.common.util.NettyBootstrapUtil;
import org.hum.nettyproxy.compoment.interceptor.NamespaceRobberHandler;
import org.hum.nettyproxy.compoment.interceptor.NamespaceRobberHandler.Interceptor;
import org.hum.nettyproxy.compoment.interceptor.impl.NettyProxyComRobberHandler;
import org.hum.nettyproxy.compoment.monitor.NettyProxyMonitorHandler;
import org.hum.nettyproxy.core.NettyProxyContext;
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

public class NettyHttpSimpleProxyServer implements Runnable  {

	private static final Logger logger = LoggerFactory.getLogger(NettyHttpSimpleProxyServer.class);
	private final String HttpSimpleServerThreadNamePrefix = RunModeEnum.HttpSimpleProxy.getName();
	
	private final ServerBootstrap serverBootstrap;
	private final HttpChannelInitializer httpChannelInitializer;
	private final NettyProxyConfig config;

	public NettyHttpSimpleProxyServer(NettyProxyConfig config) {
		this.config = config;
		serverBootstrap = new ServerBootstrap();
		httpChannelInitializer = new HttpChannelInitializer();
		NettyProxyContext.regist(config);
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
				logger.info("http-simple-server started, listening port: " + config.getPort());
			}
		});
	}
	
	private static class HttpChannelInitializer extends ChannelInitializer<Channel> {
		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addFirst(new NettyProxyMonitorHandler());
			Map<String, Interceptor> regxMap = new HashMap<String, NamespaceRobberHandler.Interceptor>();
			regxMap.put("nettyproxy.com", new NettyProxyComRobberHandler());
			ch.pipeline().addFirst(new NamespaceRobberHandler(regxMap));
			ch.pipeline().addLast(new HttpRequestDecoder()).addLast(new HttpProxyProcessHandler());
		}
	}
}
