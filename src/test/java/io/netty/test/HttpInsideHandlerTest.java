package io.netty.test;

import org.hum.jladder.adapter.http.common.HttpConstant;
import org.hum.jladder.adapter.http.insideproxy.HttpInsideLocalHandler;
import org.hum.jladder.adapter.http.insideproxy.ProxyEncryptHandler;
import org.hum.jladder.adapter.http.wrapper.HttpRequestWrapperHandler;
import org.hum.jladder.common.core.NettyProxyContext;
import org.hum.jladder.common.core.config.JladderConfig;
import org.hum.jladder.common.enumtype.RunModeEnum;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpInsideHandlerTest {

	@Test
	public void test1() throws Exception {
		JladderConfig nettyConfig = new JladderConfig();
		nettyConfig.setOutsideProxyHost("47.75.102.227");
		nettyConfig.setOutsideProxyPort(5432);
		nettyConfig.setRunMode(RunModeEnum.HttpInsideServer);
		NettyProxyContext.regist(nettyConfig);
		String request = "GET / HTTP/1.1\n" + 
				"Host: www.google.com\n"
				+ "\n";
		MyEmbeddedChannel channel = new MyEmbeddedChannel(
				new io.netty.handler.codec.http.HttpRequestDecoder(), 
				new HttpObjectAggregator(HttpConstant.HTTP_OBJECT_AGGREGATOR_LEN), 
				new HttpRequestWrapperHandler(), 
				new ProxyEncryptHandler(),
				new HttpInsideLocalHandler()
				);
		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeBytes(request.getBytes());
		channel.writeInbound(byteBuf);

		while (true) {
			System.out.println(new String(readOutbound(channel)));
		}
	}
	
	private byte[] readOutbound(EmbeddedChannel channel) {
		ByteBuf readOutbound = channel.readOutbound();
		System.out.println(readOutbound.readableBytes());
		byte[] bytes = new byte[readOutbound.readableBytes()];
		readOutbound.readBytes(bytes);
		return bytes;
	}
}
