package org.jladder.test;

import java.io.IOException;

import org.jladder.adapter.protocol.JladderAsynForwardClient;
import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderChannelHandlerContext;
import org.jladder.adapter.protocol.listener.SimpleJladderAsynForwardClientListener;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.CharsetUtil;

public class JladderAsynForwardClientTest {

	@Test
	public void test1() throws IOException, InterruptedException {
		JladderAsynForwardClient client = new JladderAsynForwardClient("www.baidu.com", 80, new NioEventLoopGroup(1));
		client.addListener(new SimpleJladderAsynForwardClientListener() {
			@Override
			public void onReceiveData(JladderByteBuf jladderByteBuf) {
				System.out.println(jladderByteBuf.toByteBuf().toString(CharsetUtil.UTF_8));
			}

			@Override
			public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
				System.out.println("disconnect");
			}
		});
		ByteBuf message = Unpooled.wrappedBuffer("GET / HTTP/1.1\r\nConnection: close\r\n\r\n".getBytes());
		client.writeAndFlush(message);
		
		System.in.read();
	}
	
	@Test
	public void test2() throws IOException, InterruptedException {
		JladderAsynForwardClient client = new JladderAsynForwardClient("news.cssn.cn", 80, new NioEventLoopGroup(1));
		client.addListener(new SimpleJladderAsynForwardClientListener() {
			@Override
			public void onReceiveData(JladderByteBuf jladderByteBuf) {
				System.out.println(jladderByteBuf.toByteBuf().toString(CharsetUtil.UTF_8));
			}
			
			@Override
			public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
				System.out.println("disconnect");
			}
		});
		ByteBuf message = Unpooled.wrappedBuffer(("GET http://news.cssn.cn/images/mobile_v2020.js HTTP/1.1\r\n" + 
				"Host:news.cssn.cn\r\n" + 
				"User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:83.0) Gecko/20100101 Firefox/83.0\r\n" + 
				"Accept:*/*\n" + 
				"Accept-Language:zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\r\n" + 
				"Accept-Encoding:gzip, deflate\r\n" + 
				"Connection:keep-alive\r\n" + 
				"content-length:0\r\n" + 
				"x-forwarded-for:/0:0:0:0:0:0:0:1:62713\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"").getBytes());
		client.writeAndFlush(message);
		
		System.in.read();
	}
}
