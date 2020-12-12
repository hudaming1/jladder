package org.jladder.adapter.outside;

import org.jladder.adapter.protocol.executor.JladderCryptoInHandler;
import org.jladder.adapter.protocol.executor.JladderCryptoOutHandler;
import org.jladder.common.NamedThreadFactory;
import org.jladder.common.core.NettyProxyContext;
import org.jladder.common.core.config.JladderConfig;
import org.jladder.common.enumtype.RunModeEnum;
import org.jladder.common.util.NettyBootstrapUtil;
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
 * 翻墙双服务器（墙外服务器）
 * @author hudaming
 */
public class NettyOutsideProxyServer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(NettyOutsideProxyServer.class);
	private final String OutSideServerThreadNamePrefix = RunModeEnum.OutsideServer.getName();
	
	private final ServerBootstrap serverBootstrap;
	private final HttpChannelInitializer httpChannelInitializer;
	private final JladderConfig config;

	public NettyOutsideProxyServer(JladderConfig config) {
		this.config = config;
		NettyProxyContext.regist(config);
		serverBootstrap = new ServerBootstrap();
		httpChannelInitializer = new HttpChannelInitializer();
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
		private final JladderCryptoOutHandler jladderCryptoHandler = new JladderCryptoOutHandler();
		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addLast(new JladderCryptoInHandler());
			ch.pipeline().addLast(jladderCryptoHandler);
//			ch.pipeline().addLast(new NettyOutsideHandler());
			ch.pipeline().addLast(new NettyOutsideSyncHandler());
		}
	}
}