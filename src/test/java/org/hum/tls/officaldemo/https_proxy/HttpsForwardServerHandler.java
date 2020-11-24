package org.hum.tls.officaldemo.https_proxy;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;

public class HttpsForwardServerHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };
	
	private String host;
	private int port;
	
	public HttpsForwardServerHandler(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		
		System.out.println("HttpsForwardServerHandler.channelRead0.enter");
		
		if (msg instanceof DefaultHttpRequest) {
			DefaultHttpRequest req = (DefaultHttpRequest) msg;
			
			System.out.println("=================================");
			System.out.println(req);
			System.out.println("=================================");

//			boolean keepAlive = HttpUtil.isKeepAlive(req);
			
			
//			FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), OK, Unpooled.wrappedBuffer(CONTENT));
			FullHttpResponse response = HttpsClient.send(host, port, (HttpRequest) msg);
			System.out.println("resp=" + response);

//			if (keepAlive) {
//				if (!req.protocolVersion().isKeepAliveDefault()) {
//					response.headers().set(CONNECTION, KEEP_ALIVE);
//				}
//			} else {
				response.headers().set(CONNECTION, CLOSE);
//			}
			ctx.writeAndFlush(response);

//			if (!keepAlive) {
//				f.addListener(ChannelFutureListener.CLOSE);
//			}
		}
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
