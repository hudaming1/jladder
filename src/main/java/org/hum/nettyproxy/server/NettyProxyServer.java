package org.hum.nettyproxy.server;

import org.hum.nettyproxy.common.Config;
import org.hum.nettyproxy.common.codec.NettyProxyConnectMessageCodec;
import org.hum.nettyproxy.server.handler.NettyServerPipeChannelHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyProxyServer {

	public static void main(String[] args) {
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 10));
		serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new NettyProxyConnectMessageCodec.Decoder());
				ch.pipeline().addLast(new NettyServerPipeChannelHandler());
			}
		});
		serverBootstrap.bind(Config.PROXY_PORT);
		System.out.println("proxy-server started, listenning port:" + Config.PROXY_PORT);
	}
}