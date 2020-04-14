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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

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
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				ctx.pipeline().remove(this);
				ctx.writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()));
				ctx.pipeline().addLast(sslCtx.newHandler(ch.alloc()));
				System.out.println("connected1 " + msg);
//				ByteBuf buf = (ByteBuf) msg;
//				byte[] bytes = new byte[buf.readableBytes()];
//				buf.readBytes(bytes);
//				System.out.println("\n" + new String(bytes) + "\n");
//				String respString = "HTTP/1.1 200 OK\n"
//						+ "Content-type:text/html\n"
//						+ "Content-length:" + bytes.length + "\n"
//						+ "\n";
//				buf.writeBytes(respString.getBytes());
//				buf.writeBytes(bytes);
//    			ctx.writeAndFlush(buf);
//    			System.out.println("flush");
				ctx.pipeline().addLast(new ChannelInboundHandlerAdapter() {
	                @Override
	                public void channelRead(ChannelHandlerContext ctx, Object _msg) throws Exception {
	                	System.out.println("connect2");
	    				ByteBuf buf = (ByteBuf) _msg;
	    				String body = "hello world";
	    				String respString = "HTTP/1.1 200 OK\n"
	    						+ "Content-type:text/html\n"
	    						+ "Content-length:" + body.length() + "\n"
	    						+ "\n";
	    				buf.writeBytes(respString.getBytes());
	    				buf.writeBytes(body.getBytes());
	        			ctx.writeAndFlush(buf);
//	    				byte[] bytes = new byte[buf.readableBytes()];
//	    				buf.readBytes(bytes);
//	    				// System.out.println(Arrays.toString(bytes));
//	    				System.out.println(new String(bytes));
	                    
	                }
	            });
			}
		});
		
		// 如果不用https代理，则需要注释以下代码
        if (sslCtx != null) {
//            p.addLast(new ChannelInboundHandlerAdapter() {
//                @Override
//                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                    ctx.pipeline().remove(this);
//
//    				ByteBuf buf = (ByteBuf) msg;
//    				String body = "hello world";
//    				String respString = "HTTP/1.1 200 OK\n"
//    						+ "Content-type:text/html\n"
//    						+ "Content-length:" + body.length() + "\n"
//    						+ "\n";
//    				buf.writeBytes(respString.getBytes());
//    				buf.writeBytes(body.getBytes());
//        			ctx.writeAndFlush(buf);
////    				byte[] bytes = new byte[buf.readableBytes()];
////    				buf.readBytes(bytes);
////    				// System.out.println(Arrays.toString(bytes));
////    				System.out.println(new String(bytes));
//                    
//                }
//            });
            //p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        
//        p.addLast(new HttpServerCodec());
//
//		// 如果不用https代理，则需要注释以下代码
//        p.addLast(new HttpServerExpectContinueHandler());
//        
//        p.addLast(new HttpHelloWorldServerHandler());
    }
}
