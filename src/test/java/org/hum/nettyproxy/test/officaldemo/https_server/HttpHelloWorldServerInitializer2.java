package org.hum.nettyproxy.test.officaldemo.https_server;

import org.hum.nettyproxy.test.officaldemo.https_server.HttpHelloWorldServerInitializer.LogInboundAdapter;
import org.hum.nettyproxy.test.officaldemo.https_server.HttpHelloWorldServerInitializer.LogOutboundAdapter;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;

public class HttpHelloWorldServerInitializer2 extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public HttpHelloWorldServerInitializer2(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
		p.addLast(new LogInboundAdapter());
		p.addLast(new LogOutboundAdapter());
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpHelloWorldServerHandler());
    }
}