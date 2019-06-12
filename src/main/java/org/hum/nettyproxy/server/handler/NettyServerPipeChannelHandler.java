package org.hum.nettyproxy.server.handler;

import org.hum.nettyproxy.common.Config;
import org.hum.nettyproxy.common.codec.DynamicLengthDecoder;
import org.hum.nettyproxy.common.codec.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.codec.NettyProxyConnectMessageCodec;
import org.hum.nettyproxy.common.codec.NettyProxyConnectMessageCodec.NettyProxyConnectMessage;
import org.hum.nettyproxy.common.handler.DecryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.EncryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.ForwardHandler;
import org.hum.nettyproxy.common.handler.InactiveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(NettyServerPipeChannelHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext insideProxyCtx, NettyProxyConnectMessage msg) throws Exception {
		logger.debug("prepare connect to server[{}:{}]", msg.getHost(), msg.getPort());
		Bootstrap bootstrap = new Bootstrap();
		// 交换数据完成
		insideProxyCtx.pipeline().remove(NettyProxyConnectMessageCodec.Decoder.class);
		final Channel insideProxyChannel = insideProxyCtx.channel();
		bootstrap.group(insideProxyChannel.eventLoop()).channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Config.CONNECT_TIMEOUT);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel remoteChannel) throws Exception {
				if (msg.isHttps()) {
					// 如果目标服务器是https，则直接转发即可 (remote->inside_server)
					remoteChannel.pipeline().addLast(new ForwardHandler("remote->inside_server", insideProxyChannel), new InactiveHandler(insideProxyCtx.channel()));
				} else {
					// 如果目标服务器是http，则需要程序自己加解密转发。 (remote->inside_server)
					remoteChannel.pipeline().addLast(new EncryptPipeChannelHandler(insideProxyCtx.channel()), new InactiveHandler(insideProxyCtx.channel()));
				}
			}
		});
		// server与remote建立连接
		bootstrap.connect(msg.getHost(), msg.getPort()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture targetWebsiteChannelFuture) throws Exception {
				logger.info("connected to server[{}:{}] successfully", msg.getHost(), msg.getPort());
				
				// 如果是HTTPS协议则直接透传转发；如果是HTTP则需要程序自行进行加解密转发。
				if (msg.isHttps()) {
					// inside_server -> remote
					insideProxyChannel.pipeline().addLast(new ForwardHandler("inside_server->remote", targetWebsiteChannelFuture.channel()));
				} else {
					// 读localServer并向remote写（inside_server -> remote）
					insideProxyChannel.pipeline().addLast(new DynamicLengthDecoder(), new DecryptPipeChannelHandler(targetWebsiteChannelFuture.channel()));
				}
				
				// 告知inside_server，outside_server已经和remote建立连接成功，可以开始转发browser的数据了
				insideProxyChannel.writeAndFlush(NettyProxyBuildSuccessMessage.build(insideProxyChannel));
				// connectMessage建立完成后，就没用了，因此删除NettyServerPipeChannelHandler
				insideProxyChannel.pipeline().remove(NettyServerPipeChannelHandler.this);
			}
		});
	}
}