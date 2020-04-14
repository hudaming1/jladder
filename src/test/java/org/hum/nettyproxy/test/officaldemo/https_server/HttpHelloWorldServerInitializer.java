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
package org.hum.nettyproxy.test.officaldemo.https_server;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";

	public HttpHelloWorldServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
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
				System.out.println("connected1 " + msg);
				ctx.pipeline().addLast(new LogInboundAdapter());
				ctx.pipeline().addLast(new LogOutboundAdapter());
				ctx.pipeline().addLast(sslCtx.newHandler(ctx.alloc()));
//		        ctx.pipeline().addLast(new HttpServerCodec());
//		        ctx.pipeline().addLast(new HttpServerExpectContinueHandler());
//		        ctx.pipeline().addLast(new HttpHelloWorldServerHandler());

				ctx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()))
						.addListener(new GenericFutureListener<Future<? super Void>>() {
							@Override
							public void operationComplete(Future<? super Void> future) throws Exception {
								System.out.println("flush connect-line");
							}
						});
				ctx.pipeline().remove(this);
			}
		});
	}

	public static class LogInboundAdapter extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ByteBuf buf = (ByteBuf) msg;
			byte[] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			System.out.println("read==================");
			System.out.println(Arrays.toString(bytes));
			System.out.println(new String(bytes));
			System.out.println("readover==================");
			buf.resetReaderIndex();

//			Iterator<Entry<String, ChannelHandler>> iterator = ctx.pipeline().iterator();
//			System.out.println();
//			while (iterator.hasNext()) {
//				Entry<String, ChannelHandler> entry = iterator.next();
//				System.out.println(entry.getKey() + "=" +  entry.getValue());
//			}
//			System.out.println();

			ctx.fireChannelRead(msg);
		}
	};

	public static void main(String[] args) {
		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeBytes("Hello World".getBytes());
		byte[] barray = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(barray);
		System.out.println(new String(barray));
	}

	public static class LogOutboundAdapter extends ChannelOutboundHandlerAdapter {
		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
			try {
				ByteBuf buf = (ByteBuf) msg;
				
				byte[] barray = new byte[buf.readableBytes()];
				buf.readBytes(barray);
				
				System.out.println("write===========" + msg);
				System.out.println(Arrays.toString(barray));
				System.out.println(new String(barray));
				System.out.println("writeover===========" + msg);
				buf.resetReaderIndex();
				ctx.write(msg, promise);
			} catch (Exception ce) {
				ce.printStackTrace();
			}
		}
	};
}
