package org.hum.nettyproxy.server.handler;

import org.hum.nettyproxy.common.codec.DynamicLengthDecoder;
import org.hum.nettyproxy.common.codec.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.codec.NettyProxyConnectMessageCodec;
import org.hum.nettyproxy.common.codec.NettyProxyConnectMessageCodec.NettyProxyConnectMessage;
import org.hum.nettyproxy.common.handler.DecryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.EncryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.InactiveHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServerPipeChannelHandler extends SimpleChannelInboundHandler<NettyProxyConnectMessage> {
	
	@Override
	protected void channelRead0(ChannelHandlerContext insideProxyCtx, NettyProxyConnectMessage msg) throws Exception {
		Bootstrap bootstrap = new Bootstrap();
		// 交换数据完成
		insideProxyCtx.pipeline().remove(NettyProxyConnectMessageCodec.Decoder.class);
		final Channel insideProxyChannel = insideProxyCtx.channel();
		bootstrap.group(insideProxyChannel.eventLoop()).channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		// pipe1: 读remote并向localServer写（从remote到localServer）
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new EncryptPipeChannelHandler(insideProxyCtx.channel()), new InactiveHandler(insideProxyCtx.channel()));
			}
		});
		// server与remote建立连接
		bootstrap.connect(msg.getHost(), msg.getPort()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture targetWebsiteChannelFuture) throws Exception {
				System.out.println("proxy-server connect remote-server : " + msg.getHost() + ":" + msg.getPort());
				// pipe2: 读localServer并向remote写（从localServer到remote）
				insideProxyChannel.pipeline().addLast(new DynamicLengthDecoder(), new DecryptPipeChannelHandler(targetWebsiteChannelFuture.channel()));
				// 告知localserver，proxy已经准备好
				insideProxyChannel.writeAndFlush(NettyProxyBuildSuccessMessage.build());
				// socks协议壳已脱，因此后面转发只需要靠pipe_handler即可，因此删除SocksConnectHandler
				insideProxyChannel.pipeline().remove(NettyServerPipeChannelHandler.this);
			}
		});
	}
}