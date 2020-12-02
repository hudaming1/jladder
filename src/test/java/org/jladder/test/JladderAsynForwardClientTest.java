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
}
