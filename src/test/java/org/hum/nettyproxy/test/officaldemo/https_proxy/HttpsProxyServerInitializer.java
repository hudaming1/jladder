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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
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
				String domain = parse2Domain((ByteBuf) msg);
				// 根据域名颁发证书
				SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(domain));
				// 确保SSL握手完成后，将业务Handler加入pipeline
				sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
					@Override
					public void operationComplete(Future<? super Channel> future) throws Exception {
						ctx.pipeline().addLast(new HttpServerCodec());
						ctx.pipeline().addLast(new HttpServerExpectContinueHandler());
						ctx.pipeline().addLast(new HttpsForwardServerHandler(domain, 443));
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
			}
		});
	}
	
	private String parse2Domain(ByteBuf byteBuf) {
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);
		String requestLine = new String(bytes);
		System.out.println(requestLine.split(" ")[0]);
		String hostAndPort = requestLine.split(" ")[1];
		byteBuf.resetReaderIndex();
		return hostAndPort.split(":")[0];
	}
}
