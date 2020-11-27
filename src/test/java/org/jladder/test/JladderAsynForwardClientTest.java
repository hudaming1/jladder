package org.jladder.test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.jladder.adapter.protocol.JladderAsynForwardClient;
import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderMessageReceiveEvent;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.CharsetUtil;

public class JladderAsynForwardClientTest {

	@Test
	public void test1() throws IOException, InterruptedException {
		JladderAsynForwardClient client = new JladderAsynForwardClient("www.baidu.com", 80, new NioEventLoopGroup(1));
		ByteBuf message = Unpooled.wrappedBuffer("GET / HTTP/1.1\r\nConnection: close\r\n\r\n".getBytes());
		client.writeAndFlush(message).onReceive(new JladderMessageReceiveEvent() {
			@Override
			public void onReceive(JladderByteBuf byteBuf) {
				System.out.println(byteBuf.toByteBuf().toString(CharsetUtil.UTF_8));
			}
		});
		
		System.in.read();
	}
	
	@Test
	public void test2() throws Exception {
		
		String body1 = "GET http://www.cqcoal.com/css/mask.css HTTP/1.1\n" + 
				"Host:www.cqcoal.com\n" + 
				"User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:82.0) Gecko/20100101 Firefox/82.0\n" + 
				"Accept:text/css,*/*;q=0.1\n" + 
				"Accept-Language:zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\n" + 
				"Accept-Encoding:gzip, deflate\n" + 
				"Connection:keep-alive\n" + 
				"content-length:0\n" + 
				"x-forwarded-for:/0:0:0:0:0:0:0:1:63757\n" + 
				"\n" + 
				"\n" + 
				"";
		
		String body2 = "GET http://www.cqcoal.com/js/calculator/css/calculator.css HTTP/1.1\n" + 
				"Host:www.cqcoal.com\n" + 
				"User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:82.0) Gecko/20100101 Firefox/82.0\n" + 
				"Accept:text/css,*/*;q=0.1\n" + 
				"Accept-Language:zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\n" + 
				"Accept-Encoding:gzip, deflate\n" + 
				"Connection:keep-alive\n" + 
				"content-length:0\n" + 
				"x-forwarded-for:/0:0:0:0:0:0:0:1:63758\n" + 
				"\n" + 
				"\n";
		
		JladderAsynForwardClient client = new JladderAsynForwardClient("www.cqcoal.com", 80, new NioEventLoopGroup(1));
		CountDownLatch latch = new CountDownLatch(2);
		ExecutorService Executors = java.util.concurrent.Executors.newFixedThreadPool(2);
		Executors.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					latch.countDown();
					latch.await();
					client.writeAndFlush(Unpooled.wrappedBuffer(body1.getBytes())).onReceive(new JladderMessageReceiveEvent() {
						@Override
						public void onReceive(JladderByteBuf byteBuf) {
							System.out.println("response1");
							System.out.println(byteBuf.toByteBuf().toString(CharsetUtil.UTF_8));
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
		Executors.execute(new Runnable() {
			@Override
			public void run() {
				try {
					latch.countDown();
					latch.await();
					client.writeAndFlush(Unpooled.wrappedBuffer(body2.getBytes())).onReceive(new JladderMessageReceiveEvent() {
						@Override
						public void onReceive(JladderByteBuf byteBuf) {
							System.out.println("response2");
							System.out.println(byteBuf.toByteBuf().toString(CharsetUtil.UTF_8));
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		System.in.read();
	}
}
