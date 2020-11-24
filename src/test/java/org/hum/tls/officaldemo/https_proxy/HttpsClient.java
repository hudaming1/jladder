package org.hum.tls.officaldemo.https_proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

public class HttpsClient {

	private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
	private static final EventLoopGroup WORKER_GROUP2 = new NioEventLoopGroup(1);

	public static Bootstrap newBootStrap() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(WORKER_GROUP);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		return bootstrap;
	}
	
	public static void main(String[] args) throws Exception {
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
		FullHttpResponse resp = send("www.baidu.com", 443, httpRequest);
		System.out.println("read resp=");
		System.out.println(resp);
	}
	
	public static FullHttpResponse send(String host, int port, HttpRequest httpRequest) throws Exception {
		final Bootstrap b = newBootStrap();
		Promise<FullHttpResponse> promise = new DefaultPromise<FullHttpResponse>(WORKER_GROUP2.next());
		MainHandler mainHandler = new MainHandler(promise, httpRequest);
		b.handler(new ClientInit(mainHandler, SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()));
		b.connect(host, port);
		return promise.get();
	}

	@ChannelHandler.Sharable
	private static class MainHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

		private HttpRequest request;
		private Promise<FullHttpResponse> promise;

		public MainHandler(Promise<FullHttpResponse> promise, HttpRequest request) {
			super(false);
			this.request = request;
			this.promise = promise;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			ctx.channel().writeAndFlush(request);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
			// HttpContent httpContent = (HttpContent) msg;
			// String response = httpContent.content().toString(Charset.defaultCharset());
			promise.setSuccess(msg);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
			ctx.channel().close();
		}
	}

	private static class ClientInit extends ChannelInitializer<SocketChannel> {

		private ChannelInboundHandler handler;
		private SslContext context;

		public ClientInit(ChannelInboundHandler handler, SslContext context) {
			this.handler = handler;
			this.context = context;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			if (true) {
				ch.pipeline().addLast(context.newHandler(ch.alloc()));
			}
			ch.pipeline().addLast(new HttpResponseDecoder());
			ch.pipeline().addLast(new HttpRequestEncoder());
			ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
			ch.pipeline().addLast(handler);
		}
	}
}
