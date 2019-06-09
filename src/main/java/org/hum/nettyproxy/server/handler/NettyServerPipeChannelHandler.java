package org.hum.nettyproxy.server.handler;

import org.hum.nettyproxy.common.codec.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.codec.NettyProxyConnectMessageCodec;
import org.hum.nettyproxy.common.codec.NettyProxyConnectMessageCodec.NettyProxyConnectMessage;
import org.hum.nettyproxy.common.handler.ForwardHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServerPipeChannelHandler extends SimpleChannelInboundHandler<NettyProxyConnectMessage> {

	private final Bootstrap bootstrap = new Bootstrap();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, NettyProxyConnectMessage msg) throws Exception {
		// 交换数据完成
		ctx.pipeline().remove(NettyProxyConnectMessageCodec.Decoder.class);
		final Channel localServerChannel = ctx.channel();
		bootstrap.group(localServerChannel.eventLoop()).channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		// pipe1: 读remote并向localServer写（从remote到localServer）
		bootstrap.handler(new ForwardHandler(localServerChannel));
		// server与remote建立连接
		bootstrap.connect(msg.getHost(), msg.getPort()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture remoteChannelFuture) throws Exception {
				// pipe2: 读localServer并向remote写（从localServer到remote）
				localServerChannel.pipeline().addLast(new ForwardHandler(remoteChannelFuture.channel()));
				// 告知localserver，proxy已经准备好
				localServerChannel.writeAndFlush(NettyProxyBuildSuccessMessage.build());
				// socks协议壳已脱，因此后面转发只需要靠pipe_handler即可，因此删除SocksConnectHandler
				localServerChannel.pipeline().remove(NettyServerPipeChannelHandler.this);
			}
		});
	}
}