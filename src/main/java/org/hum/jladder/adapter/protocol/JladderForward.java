package org.hum.jladder.adapter.protocol;

import org.hum.jladder.adapter.protocol.listener.JladderConnectListener;
import org.hum.jladder.adapter.protocol.listener.JladderReadListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class JladderForward {
	
	private EventLoopGroup eventLoopGroup;
	private String proxyHost;
	private int proxyPort;
	
	public JladderForward(String proxyHost, int proxyPort) {
		this(proxyHost, proxyPort, new NioEventLoopGroup());
	}
	
	public JladderForward(String proxyHost, int proxyPort, EventLoopGroup eventLoopGroup) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.eventLoopGroup = eventLoopGroup;
	}

	public JladderForward connect(String host, int port, JladderConnectListener listener) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(eventLoopGroup);
		// NettyBootstrapUtil.initTcpServerOptions(bootstrap, Config);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new JladderCodecHandler());
			}
		});	
		// 建立连接
		bootstrap.connect(proxyHost, proxyPort).addListener(listener);
		return this;
	}

	public JladderForward onRead(JladderReadListener jladderReadListener) {
		
		return this;
	}
}
