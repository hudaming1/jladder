package org.hum.nettyproxy.http;

import org.hum.nettyproxy.common.NamedThreadFactory;
import org.hum.nettyproxy.http.codec.HttpRequestDecoder;
import org.hum.nettyproxy.http.handler.HttpProxyProcessHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyHttpProxy implements Runnable {
	
	private int port;
	private int workerCnt;
	private final ServerBootstrap serverBootstrap = new ServerBootstrap();
	private ChannelInitializer<Channel> channelInitializer = new HttpChannelInitializer();

	public NettyHttpProxy(int port) {
		this.port = port;
	}
	
	public NettyHttpProxy(int port, int workerCnt) {
		this.port = port;
		this.workerCnt = workerCnt;
	}
	
	@Override
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, new NamedThreadFactory("http-boss-thread"));
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(workerCnt, new NamedThreadFactory("http-worker-thread"));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.group(bossGroup, workerGroup);
		serverBootstrap.childHandler(channelInitializer);
		// TODO 考虑使用TCP相关参数对代理调优
		serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				System.out.println("server started, listening port: " + port);
			}
		});
	}
	
	private static class HttpChannelInitializer extends ChannelInitializer<Channel> {
		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addLast(new HttpRequestDecoder()).addLast(new HttpProxyProcessHandler());
		}
	}
}
