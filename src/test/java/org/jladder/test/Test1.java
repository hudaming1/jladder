package org.jladder.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jladder.common.enumtype.RunModeEnum;
import org.jladder.config.JladderContext;
import org.jladder.config.JladderFullConfig;
import org.jladder.core.executor.JladderForwardExecutor;
import org.jladder.core.message.JladderMessageBuilder;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Test1 {
	
	@Test
	public void test1() throws IOException, InterruptedException {
		JladderFullConfig jladderConfig = new JladderFullConfig(RunModeEnum.HttpInsideServer, 10086);
		jladderConfig.setOutsideProxyHost("localhost");
		jladderConfig.setOutsideProxyPort(5432);
		JladderContext.regist(jladderConfig);
		AtomicInteger counter = new AtomicInteger(0);
		
		JladderForwardExecutor JladderForwardExecutor = new JladderForwardExecutor();
		
		Thread.sleep(3000);
		for (int i = 0 ;i < 1000 ;i ++) {
			ByteBuf byteBuf = Unpooled.buffer();
			byteBuf.writeBytes("hello ".getBytes());
			JladderForwardExecutor.writeAndFlush(JladderMessageBuilder.buildUnNeedEncryptMessage(1, "123", "www.baidu.com", 443, byteBuf)).onReceive(resp -> {
				int len = resp.readableBytes();
				byte[] bytes = new byte[len];
				resp.readBytes(bytes);
				System.out.println(counter.incrementAndGet() + "." + new String(bytes));
			});
		}
		
		System.in.read();
	}

	@Test
	public void test2() throws IOException, InterruptedException {
		JladderFullConfig jladderConfig = new JladderFullConfig(RunModeEnum.HttpInsideServer, 10086);
		jladderConfig.setOutsideProxyHost("localhost");
		jladderConfig.setOutsideProxyPort(5432);
		JladderContext.regist(jladderConfig);
		AtomicInteger counter = new AtomicInteger(0);
		
		JladderForwardExecutor JladderForwardExecutor = new JladderForwardExecutor();
		
		Thread.sleep(3000);
		for (int i = 0 ;i < 1000 ;i ++) {
			ByteBuf byteBuf = Unpooled.buffer();
			byteBuf.writeBytes("hello ".getBytes());
			JladderForwardExecutor.writeAndFlush(JladderMessageBuilder.buildUnNeedEncryptMessage(1, "123", "www.baidu.com", 443, byteBuf)).onReceive(resp -> {
				int len = resp.readableBytes();
				byte[] bytes = new byte[len];
				resp.readBytes(bytes);
				System.out.println(counter.incrementAndGet() + "." + new String(bytes));
			});
		}
		
		System.in.read();
	}
	
	@Test
	public void test3() throws UnknownHostException, IOException, InterruptedException {
		String request = "GET / HTTP/1.1\r\n" + 
				"Host:www.baidu.com\r\n"
				+ "\r\n";
		Socket socket = new Socket("localhost", 10086);
		socket.getOutputStream().write(request.getBytes());
		socket.getOutputStream().flush();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if ("</body></html>".equals(line)) {
				break;
			}
		}
		
		Thread.sleep(3000);

		socket.getOutputStream().write(request.getBytes());
		socket.getOutputStream().flush();
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if ("</body></html>".equals(line)) {
				break;
			}
		}
		
		socket.close();
	}
}
