package org.jladder.test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.jladder.adapter.protocol.JladderAsynForwardClient;
import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.listener.JladderOnReceiveDataListener.JladderMessageReceiveEvent;
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
		
		String body1 = "GET http://www.cqcoal.com/js/echarts/echarts.js HTTP/1.1\n" + 
				"Host:www.cqcoal.com\n" + 
				"User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:82.0) Gecko/20100101 Firefox/82.0\n" + 
				"Accept-Language:zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\n" + 
				"Accept-Encoding:gzip, deflate\n" + 
				"Connection:keep-alive\n" + 
				"content-length:0\n" + 
				"x-forwarded-for:/0:0:0:0:0:0:0:1:63757\n" + 
				"\n" + 
				"\n" + 
				"";
		
		String body2 = "GET http://www.cqcoal.com/index/jquery-1.9.1.min.js HTTP/1.1\n" + 
				"Host:www.cqcoal.com\n" + 
				"User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:82.0) Gecko/20100101 Firefox/82.0\n" + 
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
	
	@Test
	public void test3() throws Exception {
		
		AtomicInteger num = new AtomicInteger(0);
		
		String body1 = "GET /img/qr_12cm.jpg HTTP/1.1\n" + 
				"Host: www.cqcoal.com\n" + 
				"Connection: keep-alive\n" + 
				"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36\n" + 
				"Accept: image/avif,image/webp,image/apng,image/*,*/*;q=0.8\n" + 
				"Accept-Encoding: gzip, deflate\n" + 
				"Accept-Language: zh-CN,zh;q=0.9\n"
				+ "\n";
		
		String body2 = "GET /img/nav_1.png HTTP/1.1\n" + 
				"Host: www.cqcoal.com\n" + 
				"Connection: keep-alive\n" + 
				"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36\n" + 
				"Accept: image/avif,image/webp,image/apng,image/*,*/*;q=0.8\n" + 
				"Accept-Encoding: gzip, deflate\n" + 
				"Accept-Language: zh-CN,zh;q=0.9\n"
				+ "\n";
		
		JladderAsynForwardClient client = new JladderAsynForwardClient("www.cqcoal.com", 80, new NioEventLoopGroup(1));
		CountDownLatch latch = new CountDownLatch(1);
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
							num.addAndGet((byteBuf.toByteBuf().readableBytes()));
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
							num.addAndGet(byteBuf.toByteBuf().readableBytes());
							System.out.println(byteBuf.toByteBuf().toString(CharsetUtil.UTF_8));
							System.out.println(num.get());
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
