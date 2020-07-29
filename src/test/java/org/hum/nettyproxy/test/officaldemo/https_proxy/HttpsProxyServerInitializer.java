/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.hum.nettyproxy.test.officaldemo.https_proxy;

import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.handler.ForwardHandler;
import org.hum.nettyproxy.common.handler.InactiveHandler;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.util.NettyBootstrapUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpsProxyServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";

	public HttpsProxyServerInitializer() {
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();

		// 如果需要https代理，则开启这个handler(如果不用https代理，则需要注释以下代码)
		p.addLast(new ChannelInboundHandlerAdapter() {
			/**
			 * 这里的msg是CONNECT方法头
			 */
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//				String[] req = parse2Domain((ByteBuf) msg);
				HttpRequest request = HttpHelper.decode((ByteBuf) msg);
				
				if (request.getMethod().equalsIgnoreCase("CONNECT")) {
					// 根据域名颁发证书
					SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(request.getHost()));
					// 确保SSL握手完成后，将业务Handler加入pipeline
					sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
						@Override
						public void operationComplete(Future<? super Channel> future) throws Exception {
							ctx.pipeline().addLast(new HttpServerCodec());
							ctx.pipeline().addLast(new HttpServerExpectContinueHandler());
							ctx.pipeline().addLast(new HttpsForwardServerHandler(request.getHost(), 443));
						}
					});
					ctx.pipeline().addLast("sslHandler", sslHandler);
					ctx.pipeline().remove(this);

					// 注意：这里要用first啊，pipeline顺序不要错
					ctx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()))
							.addListener(new GenericFutureListener<Future<? super Void>>() {
								@Override
								public void operationComplete(Future<? super Void> future) throws Exception {
									System.out.println("flush connect-line");
								}
							});
				} else {
					// 建立远端转发连接（远端收到响应后，一律转发给本地）
					Forward forward = new Forward(ctx, request.getHost(), 80);
					forward.start().addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture remoteFuture) throws Exception {
							// forward request
							remoteFuture.channel().pipeline().firstContext().writeAndFlush(msg);
							System.err.println("=============HTTP_REQUEST_BEGIN=============");
							System.err.println(request);
							System.err.println("=============HTTP_REQUEST_END=============");
						}
					});
				}
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
			NettyBootstrapUtil.initTcpServerOptions(bootStrap, NettyProxyContext.getConfig());
			bootStrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new ForwardHandler(ctx.channel()), new InactiveHandler(ctx.channel()));
				}
			});
		}
		
		public ChannelFuture start() {
			return bootStrap.connect(host, port);
		}
	}
}
