package org.hum.nettyproxy.adapter.http.handler;

import org.hum.nettyproxy.adapter.http.codec.HttpRequestDecoder;
import org.hum.nettyproxy.adapter.http.model.HttpRequest;
import org.hum.nettyproxy.common.handler.ForwardHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class HttpProxyProcessHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	
	@Override
	protected void channelRead0(ChannelHandlerContext localCtx, HttpRequest req) throws Exception {

		if (req.getHost() == null || req.getHost().isEmpty()) {
			return ;
		}
		
		// 建立远端转发连接（远端收到响应后，一律转发给本地）
		Forward forward = new Forward(localCtx, req.getHost(), req.getPort());
		System.out.println("connect " + req.getHost() + ":" + req.getPort());
		
		if (!"CONNECT".equals(req.getMethod())) {
			// 针对普通HTTP协议，用直接转发的逻辑
			forward.start().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture remoteFuture) throws Exception {
					// forward request
					remoteFuture.channel().writeAndFlush(req.getByteBuf());
				}
			});
			return ;
		}
		
		/** 针对Https协议，用另一套逻辑 **/
		// 因为https在后面建立ssl认证时，全部基于tcp协议，无法使用http，因此这里需要将http-decoder删除。
		localCtx.pipeline().remove(HttpRequestDecoder.class);
		// 因为当前handler是基于http协议的，因此也无法处理后续https通信了。
		localCtx.pipeline().remove(this);
		
		// 建立连接
		forward.start().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture remoteFuture) throws Exception {
				// 与服务端建立连接完成后，告知浏览器Connect成功，可以进行ssl通信了
				localCtx.writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes())); // TODO 待优化，在direct上分配
				// 建立转发 (browser -> server)
				localCtx.pipeline().addLast(new ForwardHandler(remoteFuture.channel()));
			}
		});
	}
	
	private static class Forward {
		private Bootstrap bootStrap = null;
		private String host;
		private int port;
		public Forward(ChannelHandlerContext ctx, String host, int port) {
			this.host = host;
			this.port = port;
			bootStrap = new Bootstrap();
			bootStrap.channel(NioSocketChannel.class);
			bootStrap.group(ctx.channel().eventLoop());
			bootStrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new ForwardHandler(ctx.channel()));
				}
			});
		}
		
		public ChannelFuture start() {
			return bootStrap.connect(host, port);
		}
	}
}
