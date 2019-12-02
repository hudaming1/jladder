package org.hum.nettyproxy.adapter.socks5.handler;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.codec.customer.DynamicLengthDecoder;
import org.hum.nettyproxy.common.codec.customer.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.codec.customer.NettyProxyConnectMessageCodec;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.core.config.NettyProxyConfig;
import org.hum.nettyproxy.common.handler.DecryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.EncryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.ForwardHandler;
import org.hum.nettyproxy.common.handler.InactiveHandler;
import org.hum.nettyproxy.common.util.NettyBootstrapUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdResponse;
import io.netty.handler.codec.socks.SocksCmdStatus;

@Sharable
public class SocksInsideServerHandler extends SimpleChannelInboundHandler<SocksCmdRequest> {

//	private Boolean isEnableAuthority = NettyProxyContext.getConfig().getEnableAuthority();
//	private HttpAuthorityCheckHandler authorityHandler = new HttpAuthorityCheckHandler(AuthManager.getInstance());
	
	@Override
	protected void channelRead0(final ChannelHandlerContext browserCtx, final SocksCmdRequest msg) throws Exception {

		if (msg.host() == null || msg.host().isEmpty()) {
			browserCtx.close();
			return;
		}
		
		NettyProxyConfig config = NettyProxyContext.getConfig();

		if (msg.port() == Constant.DEFAULT_HTTPS_PORT) {
			browserCtx.pipeline().remove(this);
		}

		// 如果开启了权限校验 TODO
//		if (isEnableAuthority != null && isEnableAuthority == true) {
//			browserCtx.pipeline().addLast(authorityHandler);
//			browserCtx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS, SocksAddressType.IPv4));
//			return ;
//		}
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(browserCtx.channel().eventLoop());
		bootstrap.channel(NioSocketChannel.class);
		NettyBootstrapUtil.initTcpServerOptions(bootstrap, config);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new PrepareConnectChannelHandler(browserCtx, msg));
			}
		});
		bootstrap.connect(config.getOutsideProxyHost(), config.getOutsideProxyPort()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture outsideServerChannelFuture) throws Exception {

				byte[] hostBytes = msg.host().getBytes();
				ByteBuf byteBuf = outsideServerChannelFuture.channel().alloc().directBuffer();
				
				outsideServerChannelFuture.channel().writeAndFlush(NettyProxyConnectMessageCodec.EncoderUtil.encode(byteBuf, hostBytes, (short) msg.port()));
			}
		});
	}
	
	private static class PrepareConnectChannelHandler extends ChannelInboundHandlerAdapter {
		
		private ChannelHandlerContext browserCtx;
		private SocksCmdRequest req;
		public PrepareConnectChannelHandler(ChannelHandlerContext browserCtx, SocksCmdRequest req) {
			this.browserCtx = browserCtx;
			this.req = req;
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

	        outsideProxyCtx.pipeline().remove(this);
	        
	        Channel browserChannel = browserCtx.channel();

	        if (req.port() == Constant.DEFAULT_HTTPS_PORT) { 
	        	outsideProxyCtx.pipeline().addLast(new ForwardHandler("outside_server->browser", browserChannel), new InactiveHandler(browserChannel));
	        	browserChannel.pipeline().addLast(new ForwardHandler("browser->ouside_server", outsideProxyCtx.channel()));
				// 与服务端建立连接完成后，告知浏览器Connect成功，可以进行ssl通信了
				browserCtx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS, SocksAddressType.IPv4));
	        	return ;
	        } 
	        
	        // proxy.response -> browser (仅开启单项转发就够了，因为HTTP是请求/应答协议)
	        outsideProxyCtx.pipeline().addLast(new DynamicLengthDecoder(), new DecryptPipeChannelHandler(browserChannel), new InactiveHandler(browserChannel));
	        browserCtx.pipeline().addLast(new EncryptPipeChannelHandler(outsideProxyCtx.channel()), new InactiveHandler(outsideProxyCtx.channel()));
			// 与proxy-server握手完成后，告知browser socks协议结束，后面可以开始发送真正数据了(为了保证数据传输正确性，flush最好还是放到后面)
			browserCtx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS, SocksAddressType.IPv4));
		}
	}
}
