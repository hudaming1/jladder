package org.hum.nettyproxy.adapter.socks5.handler;

import org.hum.nettyproxy.common.Config;
import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.codec.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.handler.DecryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.EncryptPipeChannelHandler;
import org.hum.nettyproxy.core.ConfigContext;
import org.hum.nettyproxy.core.NettyProxyConfig;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdResponse;
import io.netty.handler.codec.socks.SocksCmdStatus;

public class ServerPipeChannelHandler extends SimpleChannelInboundHandler<SocksCmdRequest> {

	@Override
	protected void channelRead0(final ChannelHandlerContext browserCtx, final SocksCmdRequest msg) throws Exception {

		NettyProxyConfig config = ConfigContext.getConfig();
		
		System.out.println("connect " + msg.host() + ":" + msg.port());
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(browserCtx.channel().eventLoop());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast( new PrepareConnectChannelHandler(browserCtx));
			}
		});
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Config.CONNECT_TIMEOUT);
		bootstrap.connect("localhost", 5432).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture proxyServerChannelFuture) throws Exception {
				// 将ip和port输出到proxy-server
				ByteBuf directBuffer = proxyServerChannelFuture.channel().alloc().directBuffer();
				directBuffer.writeInt(Constant.MAGIC_NUMBER);
				directBuffer.writeInt(msg.host().length());
				directBuffer.writeBytes(msg.host().getBytes());
				directBuffer.writeShort((short) msg.port());
				proxyServerChannelFuture.channel().writeAndFlush(directBuffer);
				System.out.println("inside-socks-server forward bytes");
			}
		});
	}
	
	private static class PrepareConnectChannelHandler extends ChannelInboundHandlerAdapter {
		
		private ChannelHandlerContext browserCtx;
		public PrepareConnectChannelHandler(ChannelHandlerContext browserCtx) {
			this.browserCtx = browserCtx;
		}

		@Override
	    public void channelRead(ChannelHandlerContext outsideProxyCtx, Object msg) throws Exception {
			
			ByteBuf byteBuf = (ByteBuf) msg; // msg-value.type = NettyProxyBuildSuccessMessage
	        
	        // 收到对端的BuildSuccessMessage，说明Proxy已经和目标服务器建立连接成功
	        if (byteBuf.readInt() != Constant.MAGIC_NUMBER || byteBuf.readInt() != NettyProxyBuildSuccessMessage.SUCCESS) {
	        	outsideProxyCtx.close();
	        	browserCtx.close();
	        	return ;
	        }
			System.out.println("outside-server connected!");
			
			// 开启数据转发管道，读proxy并向browser写（proxy->browser）
			outsideProxyCtx.pipeline().addLast(new DecryptPipeChannelHandler(browserCtx.channel()));
			outsideProxyCtx.pipeline().remove(PrepareConnectChannelHandler.class);
			// 读browser并向proxy写（从browser到proxy）
			browserCtx.pipeline().addLast(new EncryptPipeChannelHandler(outsideProxyCtx.channel()));
			// 与proxy-server握手完成后，告知browser socks协议结束，后面可以开始发送真正数据了(为了保证数据传输正确性，flush最好还是放到后面)
			browserCtx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS, SocksAddressType.IPv4));
		}
	}
}
