package org.hum.nettyproxy.test.officaldemo.https_proxy;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class HttpsForwardServerHandler extends SimpleChannelInboundHandler<HttpObject> {

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
			FullHttpResponse response = HttpsClient.send(host, port, (HttpRequest) msg);
			System.out.println("resp=" + response);

			System.out.println("==============HTTPS_BEGIN===================");
			System.out.println(req);
			System.out.println();
			System.out.println();
			System.out.println(response);
			System.out.println("==============HTTPS_END=========");
			response.headers().set(CONNECTION, CLOSE);
			ctx.writeAndFlush(response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
