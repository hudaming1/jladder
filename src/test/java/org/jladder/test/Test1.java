package org.jladder.test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jladder.adapter.protocol.executor.JladderForwardExecutor;
import org.jladder.adapter.protocol.message.JladderMessage;
import org.jladder.common.core.NettyProxyContext;
import org.jladder.common.core.config.JladderConfig;
import org.jladder.common.enumtype.RunModeEnum;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Test1 {
	
	@Test
	public void test1() throws IOException, InterruptedException {
		JladderConfig jladderConfig = new JladderConfig(RunModeEnum.HttpInsideServer, 10086);
		jladderConfig.setOutsideProxyHost("localhost");
		jladderConfig.setOutsideProxyPort(5432);
		NettyProxyContext.regist(jladderConfig);
		AtomicInteger counter = new AtomicInteger(0);
		
		JladderForwardExecutor JladderForwardExecutor = new JladderForwardExecutor();
		
		Thread.sleep(3000);
		for (int i = 0 ;i < 1000 ;i ++) {
			ByteBuf byteBuf = Unpooled.buffer();
			byteBuf.writeBytes("hello ".getBytes());
			JladderForwardExecutor.writeAndFlush(JladderMessage.buildUnNeedEncryptMessage("123", "www.baidu.com", 443, byteBuf)).onReceive(resp -> {
				int len = resp.readableBytes();
				byte[] bytes = new byte[len];
				resp.readBytes(bytes);
				System.out.println(counter.incrementAndGet() + "." + new String(bytes));
			});
		}
		
		System.in.read();
	}
}
