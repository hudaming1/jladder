package org.hum.nettyproxy.adapter.http.insideproxy;

import org.hum.nettyproxy.adapter.http.model.HttpRequest;
import org.hum.nettyproxy.common.codec.customer.NettyProxyConnectMessageCodec;
import org.hum.nettyproxy.common.codec.http.HttpRequestDecoder;
import org.hum.nettyproxy.common.core.NettyProxyConfig;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.util.NettyBootstrapUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * HTTP/HTTPS 加密转发
 * <pre>
 *   针对HTTP请求，需要程序进行加密解密转发；而针对HTTPS请求，加解密由SSL协议完成，因此只需要透传转发。
 * </pre>
 * @author hudaming
 */
public class HttpProxyEncryptHandler extends SimpleChannelInboundHandler<HttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext browserCtx, HttpRequest req) throws Exception {

		if (req.getHost() == null || req.getHost().isEmpty()) {
			browserCtx.close();
			return;
		}
		
		NettyProxyConfig config = NettyProxyContext.getConfig();
		
		if (req.isHttps()) {
			// 因为https在后面建立ssl认证时，全部基于tcp协议，无法使用http，因此这里需要将http-decoder删除。
			browserCtx.pipeline().remove(HttpRequestDecoder.class);
			// 因为当前handler是基于http协议的，因此也无法处理后续https通信了。
			browserCtx.pipeline().remove(this);
		}
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(browserCtx.channel().eventLoop());
		NettyBootstrapUtil.initTcpServerOptions(bootstrap, config);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new NettyHttpProxyEncShakeHanlder(browserCtx.channel(), req));
			}
		});
		// 建立连接
		bootstrap.connect(config.getOutsideProxyHost(), config.getOutsideProxyPort()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture remoteFuture) throws Exception {
				
				byte[] hostBytes = req.getHost().getBytes();
				ByteBuf byteBuf = remoteFuture.channel().alloc().directBuffer();
				
				// 告诉OutsideServer连接到远端服务器的地址和端口。
				remoteFuture.channel().writeAndFlush(NettyProxyConnectMessageCodec.EncoderUtil.encode(byteBuf, hostBytes, (short) req.getPort()));
			}
		});
	}
	
}
