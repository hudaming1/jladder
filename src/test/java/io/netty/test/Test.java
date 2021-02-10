package io.netty.test;

import java.util.concurrent.ExecutionException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Test {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(new NioEventLoopGroup(1));
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
			}
		});		
		System.out.println("prepare");
		ChannelFuture connect = bootstrap.connect("www.google.com", 80);
		System.out.println("1-" + connect);
		connect.sync();
		System.out.println("2-" + connect);
	}
}
