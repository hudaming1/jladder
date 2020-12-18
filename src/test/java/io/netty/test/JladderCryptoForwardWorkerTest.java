package io.netty.test;

import java.io.IOException;

import org.jladder.adapter.protocol.executor.JladderCryptoForwardWorker;
import org.junit.Test;

import io.netty.channel.nio.NioEventLoopGroup;

public class JladderCryptoForwardWorkerTest {

	@Test
	public void test1() throws IOException {
		JladderCryptoForwardWorker worker = new JladderCryptoForwardWorker("47.75.102.227", 5432, new NioEventLoopGroup(1));
		worker.connect().onConnect(f -> {
			System.out.println(f.isSuccess());
		});
		
		System.in.read();
	}
}
