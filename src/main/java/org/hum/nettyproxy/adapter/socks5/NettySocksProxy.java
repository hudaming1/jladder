package org.hum.nettyproxy.adapter.socks5;

import org.hum.nettyproxy.adapter.socks5.handler.SocksProxyProcessHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socks.SocksInitRequestDecoder;
import io.netty.handler.codec.socks.SocksMessageEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettySocksProxy implements Runnable {

	private int port;
	private int workerCnt;
	private final ServerBootstrap serverBootstrap = new ServerBootstrap();
	private ChannelInitializer<Channel> channelInitializer = new SocksChannelInitializer();

	public NettySocksProxy(int port) {
		this.port = port;
	}

	public NettySocksProxy(int port, int workerCnt) {
		this.port = port;
		this.workerCnt = workerCnt;
	}

	@Override
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(workerCnt);
		serverBootstrap.group(bossGroup, workerGroup);
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.childHandler(channelInitializer);
		serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				System.out.println("netty-server started, listenning port:" + port);
			}
		});
	}
	
	private static class SocksChannelInitializer extends ChannelInitializer<Channel> {

		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addLast(new SocksInitRequestDecoder());
			ch.pipeline().addLast(new SocksMessageEncoder());
			ch.pipeline().addLast(new SocksProxyProcessHandler());
		}
	}
	
	public static void main(String[] args) {
		new Thread(new NettySocksProxy(3389)).start();
	}
}
