package org.hum.tls.netty.monitor;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MonitorClientTest {
	
	private static final String msg = "胡大明";

	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup(1));
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
			}
		});
		bootstrap.connect("127.0.0.1", 9999).addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				Channel channel = future.channel();
				ByteBuf byteBuf = channel.alloc().buffer();
				byteBuf.writeInt(52996);
				byteBuf.writeInt(msg.getBytes().length);
				byteBuf.writeBytes(msg.getBytes("utf-8"));
				channel.writeAndFlush(byteBuf);
				
				System.out.println("client flush " + byteBuf.writerIndex() + " bytes");
			}
		});
	}
}
