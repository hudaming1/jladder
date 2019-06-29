package org.hum.nettyproxy.test;

import org.hum.nettyproxy.adapter.http.consoleserver.enumtype.ContentTypeEnum;
import org.hum.nettyproxy.common.core.NettyProxyConfig;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.enumtype.RunModeEnum;
import org.hum.nettyproxy.common.helper.ByteBufHttpHelper;
import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpServerTest {

	public static void main(String[] args) {
		
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(4));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new TestInboundChannelHandler());
			}
		});
		serverBootstrap.bind(51996).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				NettyProxyConfig nettyProxyConfig = new NettyProxyConfig();
				nettyProxyConfig.setRunMode(RunModeEnum.HttpSimpleProxy);
				nettyProxyConfig.setWebroot("/Users/hudaming/Workspace/GitHub/netty-proxy/src/main/resources/webapps");
				NettyProxyContext.regist(nettyProxyConfig);
				System.out.println("server start, listen port: 9999");
			}
		});
	}
	
	private static class TestInboundChannelHandler extends ChannelInboundHandlerAdapter {

	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	HttpRequest req = ByteBufHttpHelper.decode((ByteBuf) msg);
	    	System.out.println(req);

//	    	String resp = "HTTP/1.1 200 \r\n Content-type:text/html \r\n\r\n <h1>Hello HttpServer</h1> \r\n";
	    	String resp = "HTTP/1.1 302 TemporaryRedirect\r\n"
					+ "Location:http://www.sssss.com/\r\n"
					+ "\r\n";
	    	ByteBuf buffer = ctx.alloc().buffer();
//	    	buffer = ByteBufHttpHelper.create307Response(buffer, "http://www.sssss.com");
	    	buffer.writeBytes(resp.getBytes());
	    	ctx.channel().writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
	    }
	}
}
