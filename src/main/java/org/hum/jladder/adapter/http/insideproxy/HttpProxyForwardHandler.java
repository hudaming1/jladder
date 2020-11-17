package org.hum.jladder.adapter.http.insideproxy;

import org.hum.jladder.adapter.http.wrapper.HttpRequestWrapper;
import org.hum.jladder.common.codec.customer.NettyProxyConnectMessageCodec;
import org.hum.jladder.common.core.NettyProxyContext;
import org.hum.jladder.common.core.config.NettyProxyConfig;
import org.hum.jladder.common.util.NettyBootstrapUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * HTTP/HTTPS 加密转发
 * <pre>
 *   针对HTTP请求，需要程序进行加密解密转发；而针对HTTPS请求，加解密由SSL协议完成，因此只需要透传转发。
 * </pre>
 * @author hudaming
 */
@Sharable
public class HttpProxyForwardHandler extends SimpleChannelInboundHandler<HttpRequestWrapper> {

	@Override
	protected void channelRead0(ChannelHandlerContext browserCtx, HttpRequestWrapper requestWrapper) throws Exception {

		if (requestWrapper.host() == null || requestWrapper.host().isEmpty()) {
			/**
			 * 这里不要close，否则用Chrome访问news.baidu.com会导致EmptyResponse
			 * 在调试时发现，decode第一个请求正常，但第二个请求则不是一个正常的http请求，此时disscard比close更有利于后面处理
			 */
			// browserCtx.close(); 
			return;
		}
		
		// 转发前记录真实IP，防止转发中丢失源IP地址
		requestWrapper.header("x-forwarded-for", browserCtx.channel().remoteAddress().toString());
		
		NettyProxyConfig config = NettyProxyContext.getConfig();
		
		if (requestWrapper.isHttps()) {
			// 因为https在后面建立ssl认证时，全部基于tcp协议，无法使用http，因此这里需要将http-decoder删除。
			browserCtx.pipeline().remove(io.netty.handler.codec.http.HttpRequestDecoder.class);
			// 因为当前handler是基于http协议的，因此也无法处理后续https通信了。
			browserCtx.pipeline().remove(this);
		}
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
//		bootstrap.group(new NioEventLoopGroup());
		bootstrap.group(browserCtx.channel().eventLoop());
		NettyBootstrapUtil.initTcpServerOptions(bootstrap, config);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new NettyHttpProxyEncShakeHanlder(browserCtx.channel(), requestWrapper));
			}
		});
		// 建立连接
		bootstrap.connect(config.getOutsideProxyHost(), config.getOutsideProxyPort()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture remoteFuture) throws Exception {
				
				byte[] hostBytes = requestWrapper.host().getBytes();
				ByteBuf byteBuf = remoteFuture.channel().alloc().directBuffer();
				
				// 告诉OutsideServer连接到远端服务器的地址和端口。
				remoteFuture.channel().writeAndFlush(NettyProxyConnectMessageCodec.EncoderUtil.encode(byteBuf, hostBytes, (short) requestWrapper.port()));
			}
		});
	}
	
}
