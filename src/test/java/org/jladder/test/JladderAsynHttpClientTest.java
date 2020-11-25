package org.jladder.test;

import java.io.IOException;

import org.jladder.adapter.protocol.JladderAsynHttpClient;
import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderMessageReceiveEvent;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.CharsetUtil;

public class JladderAsynHttpClientTest {

	@Test
	public void test1() throws IOException, InterruptedException {
		JladderAsynHttpClient client = new JladderAsynHttpClient("www.baidu.com", 80, new NioEventLoopGroup(1));
		ByteBuf message = Unpooled.wrappedBuffer("GET / HTTP/1.1\r\nConnection: close\r\n\r\n".getBytes());
		client.writeAndFlush(message).onReceive(new JladderMessageReceiveEvent() {
			@Override
			public void onReceive(JladderByteBuf byteBuf) {
				System.out.println(byteBuf.toByteBuf().toString(CharsetUtil.UTF_8));
			}
		});
		
		System.in.read();
	}
}
