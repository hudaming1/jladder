package org.hum.jladder.adapter.protocol;

import org.hum.jladder.adapter.http.insideproxy.NettyHttpProxyEncShakeHanlder;
import org.hum.jladder.common.codec.customer.NettyProxyConnectMessageCodec;
import org.hum.jladder.common.core.NettyProxyContext;
import org.hum.jladder.common.core.config.JladderConfig;
import org.hum.jladder.common.util.NettyBootstrapUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class JladderForward {
	
	private NioEventLoopGroup eventLoopGroup;
	private final static JladderConfig Config = NettyProxyContext.getConfig();	
	private String host;
	private int port;
	
	public JladderForward(String host, int port) {
		this(new NioEventLoopGroup(1), host, port);
	}
	
	public JladderForward(NioEventLoopGroup eventLoopGroup, String host, int port) {
		this.host = host;
		this.port = port;
	}

	public ChannelFuture connect() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(eventLoopGroup);
		// NettyBootstrapUtil.initTcpServerOptions(bootstrap, Config);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new NettyHttpProxyEncShakeHanlder(browserCtx.channel(), requestWrapper));
			}
		});	
		// 建立连接
		return bootstrap.connect(Config.getOutsideProxyHost(), Config.getOutsideProxyPort());
//		.addListener(new ChannelFutureListener() {
//			@Override
//			public void operationComplete(ChannelFuture remoteFuture) throws Exception {
//				ByteBuf byteBuf = remoteFuture.channel().alloc().directBuffer();
//				
//				// 告诉OutsideServer连接到远端服务器的地址和端口。
//				remoteFuture.channel().writeAndFlush(NettyProxyConnectMessageCodec.EncoderUtil.encode(byteBuf, host, port));
//			}
//		});
	}
}
