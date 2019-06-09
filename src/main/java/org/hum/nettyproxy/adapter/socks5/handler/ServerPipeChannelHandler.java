package org.hum.nettyproxy.adapter.socks5.handler;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.codec.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.handler.DecryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.EncryptPipeChannelHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdResponse;
import io.netty.handler.codec.socks.SocksCmdStatus;

public class ServerPipeChannelHandler extends SimpleChannelInboundHandler<SocksCmdRequest> {

	private final String PROXY_HOST = "47.75.102.227";
	private final int PROXY_PORT = 5432;
	
	@Override
	protected void channelRead0(final ChannelHandlerContext browserCtx, final SocksCmdRequest msg) throws Exception {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(browserCtx.channel().eventLoop());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new PrepareConnectChannelHandler(browserCtx));
			}
		});
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		bootstrap.connect(PROXY_HOST, PROXY_PORT).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture proxyServerChannelFuture) throws Exception {
				// 将ip和port输出到proxy-server
				ByteBuf directBuffer = proxyServerChannelFuture.channel().alloc().directBuffer();
				directBuffer.writeInt(Constant.MAGIC_NUMBER);
				directBuffer.writeInt(msg.host().length());
				directBuffer.writeBytes(msg.host().getBytes());
				directBuffer.writeShort((short) msg.port());
				proxyServerChannelFuture.channel().writeAndFlush(directBuffer);
			}
		});
	}
	
	private static class PrepareConnectChannelHandler extends SimpleChannelInboundHandler<NettyProxyBuildSuccessMessage> {
		
		private ChannelHandlerContext browserCtx;
		public PrepareConnectChannelHandler(ChannelHandlerContext browserCtx) {
			this.browserCtx = browserCtx;
		}

		@Override
		protected void channelRead0(ChannelHandlerContext proxyCtx, NettyProxyBuildSuccessMessage msg) throws Exception {
			// 开启数据转发管道，读proxy并向browser写（proxy->browser）
			proxyCtx.pipeline().addLast(new DecryptPipeChannelHandler("local.pipe4", browserCtx.channel()));
			proxyCtx.pipeline().remove(PrepareConnectChannelHandler.class);
			// 读browser并向proxy写（从browser到proxy）
			browserCtx.pipeline().addLast(new EncryptPipeChannelHandler("local.pipe1", proxyCtx.channel()));
			// 与proxy-server握手完成后，告知browser socks协议结束，后面可以开始发送真正数据了(为了保证数据传输正确性，flush最好还是放到后面)
			browserCtx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS, SocksAddressType.IPv4));
		}
	}
}
