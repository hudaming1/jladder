package io.netty.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class JladderCryptoForwardWorkerTest {

	static final byte[] body = new byte[] { 71, 69, 84, 32, 104, 116, 116, 112, 58, 47, 47, 117, 116, 114, 97, 99, 107, 46, 104, 101, 120, 117, 110, 46, 99, 111, 109, 47, 100, 112, 47, 100, 112, 108, 117, 115, 95, 99, 111, 110, 102, 105, 103, 95, 118, 101, 114, 49, 46, 48, 46, 49, 46, 106, 115, 32, 72, 84, 84, 80, 47, 49, 46, 49, 10, 72, 111, 115, 116, 58, 117, 116, 114, 97, 99, 107, 46, 104, 101, 120, 117, 110, 46, 99, 111, 109, 10, 85, 115, 101, 114, 45, 65, 103, 101, 110, 116, 58, 77, 111, 122, 105, 108, 108, 97, 47, 53, 46, 48, 32, 40, 77, 97, 99, 105, 110, 116, 111, 115, 104, 59, 32, 73, 110, 116, 101, 108, 32, 77, 97, 99, 32, 79, 83, 32, 88, 32, 49, 48, 46, 49, 53, 59, 32, 114, 118, 58, 49, 48, 57, 46, 48, 41, 32, 71, 101, 99, 107, 111, 47, 50, 48, 49, 48, 48, 49, 48, 49, 32, 70, 105, 114, 101, 102, 111, 120, 47, 49, 49, 48, 46, 48, 10, 65, 99, 99, 101, 112, 116, 58, 116, 101, 120, 116, 47, 104, 116, 109, 108, 44, 97, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 47, 120, 104, 116, 109, 108, 43, 120, 109, 108, 44, 97, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 47, 120, 109, 108, 59, 113, 61, 48, 46, 57, 44, 105, 109, 97, 103, 101, 47, 97, 118, 105, 102, 44, 105, 109, 97, 103, 101, 47, 119, 101, 98, 112, 44, 42, 47, 42, 59, 113, 61, 48, 46, 56, 10, 65, 99, 99, 101, 112, 116, 45, 76, 97, 110, 103, 117, 97, 103, 101, 58, 122, 104, 45, 67, 78, 44, 122, 104, 59, 113, 61, 48, 46, 56, 44, 122, 104, 45, 84, 87, 59, 113, 61, 48, 46, 55, 44, 122, 104, 45, 72, 75, 59, 113, 61, 48, 46, 53, 44, 101, 110, 45, 85, 83, 59, 113, 61, 48, 46, 51, 44, 101, 110, 59, 113, 61, 48, 46, 50, 10, 65, 99, 99, 101, 112, 116, 45, 69, 110, 99, 111, 100, 105, 110, 103, 58, 103, 122, 105, 112, 44, 32, 100, 101, 102, 108, 97, 116, 101, 10, 67, 111, 110, 110, 101, 99, 116, 105, 111, 110, 58, 107, 101, 101, 112, 45, 97, 108, 105, 118, 101, 10, 67, 111, 111, 107, 105, 101, 58, 85, 77, 95, 100, 105, 115, 116, 105, 110, 99, 116, 105, 100, 61, 49, 56, 56, 48, 56, 57, 50, 48, 48, 56, 50, 53, 49, 57, 45, 48, 52, 102, 101, 101, 50, 57, 101, 99, 57, 100, 55, 48, 54, 45, 52, 49, 50, 101, 50, 99, 51, 100, 45, 49, 97, 101, 97, 97, 48, 45, 49, 56, 56, 48, 56, 57, 50, 48, 48, 56, 51, 55, 54, 49, 59, 32, 99, 110, 95, 49, 50, 54, 51, 50, 52, 55, 55, 57, 49, 95, 100, 112, 108, 117, 115, 61, 37, 55, 66, 37, 50, 50, 100, 105, 115, 116, 105, 110, 99, 116, 95, 105, 100, 37, 50, 50, 37, 51, 65, 37, 50, 48, 37, 50, 50, 49, 56, 56, 48, 56, 57, 50, 48, 48, 56, 50, 53, 49, 57, 45, 48, 52, 102, 101, 101, 50, 57, 101, 99, 57, 100, 55, 48, 54, 45, 52, 49, 50, 101, 50, 99, 51, 100, 45, 49, 97, 101, 97, 97, 48, 45, 49, 56, 56, 48, 56, 57, 50, 48, 48, 56, 51, 55, 54, 49, 37, 50, 50, 37, 50, 67, 37, 50, 50, 117, 115, 101, 114, 70, 105, 114, 115, 116, 68, 97, 116, 101, 37, 50, 50, 37, 51, 65, 37, 50, 48, 37, 50, 50, 50, 48, 50, 51, 48, 53, 49, 49, 37, 50, 50, 37, 50, 67, 37, 50, 50, 37, 50, 52, 95, 115, 101, 115, 115, 105, 111, 110, 105, 100, 37, 50, 50, 37, 51, 65, 37, 50, 48, 48, 37, 50, 67, 37, 50, 50, 37, 50, 52, 95, 115, 101, 115, 115, 105, 111, 110, 84, 105, 109, 101, 37, 50, 50, 37, 51, 65, 37, 50, 48, 49, 54, 56, 51, 55, 55, 49, 49, 51, 54, 37, 50, 67, 37, 50, 50, 37, 50, 52, 100, 112, 37, 50, 50, 37, 51, 65, 37, 50, 48, 48, 37, 50, 67, 37, 50, 50, 37, 50, 52, 95, 115, 101, 115, 115, 105, 111, 110, 80, 86, 84, 105, 109, 101, 37, 50, 50, 37, 51, 65, 37, 50, 48, 49, 54, 56, 51, 55, 55, 49, 49, 51, 54, 37, 50, 67, 37, 50, 50, 105, 110, 105, 116, 105, 97, 108, 95, 118, 105, 101, 119, 95, 116, 105, 109, 101, 37, 50, 50, 37, 51, 65, 37, 50, 48, 37, 50, 50, 49, 54, 56, 51, 55, 54, 57, 53, 49, 49, 37, 50, 50, 37, 50, 67, 37, 50, 50, 105, 110, 105, 116, 105, 97, 108, 95, 114, 101, 102, 101, 114, 114, 101, 114, 37, 50, 50, 37, 51, 65, 37, 50, 48, 37, 50, 50, 104, 116, 116, 112, 115, 37, 51, 65, 37, 50, 70, 37, 50, 70, 119, 119, 119, 46, 98, 97, 105, 100, 117, 46, 99, 111, 109, 37, 50, 70, 108, 105, 110, 107, 37, 51, 70, 117, 114, 108, 37, 51, 68, 115, 68, 87, 88, 74, 56, 95, 117, 111, 45, 89, 49, 82, 51, 83, 100, 117, 89, 48, 81, 74, 75, 117, 70, 54, 118, 102, 121, 69, 106, 122, 106, 95, 118, 69, 79, 83, 109, 69, 90, 88, 66, 57, 95, 117, 65, 112, 110, 77, 69, 87, 65, 115, 114, 70, 66, 86, 52, 82, 122, 78, 100, 55, 120, 99, 75, 50, 53, 49, 121, 122, 72, 49, 50, 81, 51, 111, 52, 79, 99, 72, 53, 113, 49, 73, 75, 37, 50, 54, 119, 100, 37, 51, 68, 37, 50, 54, 101, 113, 105, 100, 37, 51, 68, 56, 56, 97, 57, 102, 51, 101, 99, 48, 48, 48, 100, 101, 54, 48, 54, 48, 48, 48, 48, 48, 48, 48, 51, 54, 52, 53, 99, 52, 57, 98, 55, 37, 50, 50, 37, 50, 67, 37, 50, 50, 105, 110, 105, 116, 105, 97, 108, 95, 114, 101, 102, 101, 114, 114, 101, 114, 95, 100, 111, 109, 97, 105, 110, 37, 50, 50, 37, 51, 65, 37, 50, 48, 37, 50, 50, 119, 119, 119, 46, 98, 97, 105, 100, 117, 46, 99, 111, 109, 37, 50, 50, 37, 50, 67, 37, 50, 50, 37, 50, 52, 114, 101, 99, 101, 110, 116, 95, 111, 117, 116, 115, 105, 100, 101, 95, 114, 101, 102, 101, 114, 114, 101, 114, 37, 50, 50, 37, 51, 65, 37, 50, 48, 37, 50, 50, 119, 119, 119, 46, 98, 97, 105, 100, 117, 46, 99, 111, 109, 37, 50, 50, 37, 55, 68, 10, 85, 112, 103, 114, 97, 100, 101, 45, 73, 110, 115, 101, 99, 117, 114, 101, 45, 82, 101, 113, 117, 101, 115, 116, 115, 58, 49, 10, 99, 111, 110, 116, 101, 110, 116, 45, 108, 101, 110, 103, 116, 104, 58, 48, 10, 120, 45, 102, 111, 114, 119, 97, 114, 100, 101, 100, 45, 102, 111, 114, 58, 47, 49, 50, 55, 46, 48, 46, 48, 46, 49, 58, 54, 52, 55, 54, 49, 10, 10 };
	
	@Test
	public void test1() throws IOException {
		
		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeBytes(HTTP_REQUEST.getBytes());
		
		Bootstrap bootStrap = new Bootstrap();
		bootStrap.group(new NioEventLoopGroup(1));
		bootStrap.channel(NioSocketChannel.class);
		// TODO 做成可配置的，避免长时阻塞，默认不配置的情况下，好像是30s
		bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		bootStrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new TestInboundHandler());
			}
		});
		
		bootStrap.connect("utrack.hexun.com", 80).addListener(f -> {
			ChannelFuture cf = (ChannelFuture) f;
			Thread.sleep(1000);
			cf.channel().writeAndFlush(byteBuf).addListener(f2 -> {
				System.out.println("flush result=" + f2.isSuccess());
			});
		});
		
		System.in.read();
	}
	
	public static void main(String[] args) {
		System.out.println(new String(body));
		System.out.println("=======");
	}
	
	@Test
	public void test2() throws UnknownHostException, IOException {
		Socket socket = new Socket("utrack.hexun.com", 80);
		OutputStream outputStream = socket.getOutputStream();
		outputStream.write(body);
		outputStream.flush();
		System.out.println("flush over");
		InputStream inputStream = socket.getInputStream();
		
		int b = -1;
		
		System.out.println("wait reposne");
		while ((b = inputStream.read()) != -1) {
			System.out.println(b);
		}
		
		
	}
	
	private static final String HTTP_REQUEST = "GET http://utrack.hexun.com/dp/dplus_config_ver1.0.1.js HTTP/1.1\r\n"
			+ "Host:utrack.hexun.com\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "";
	
	@Test
	public void test3() throws UnknownHostException, IOException {
		Socket socket = new Socket("utrack.hexun.com", 80);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bw.write(HTTP_REQUEST);
		bw.flush();
		
		System.out.println("flush over");
		InputStream inputStream = socket.getInputStream();
		
		int b = -1;
		
		System.out.println("wait reposne");
		while ((b = inputStream.read()) != -1) {
			System.out.println(b);
		}
		
		
	}
	
	private static class TestInboundHandler extends ChannelInboundHandlerAdapter {

	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	System.out.println("read server response = " +  msg);
	        ctx.fireChannelRead(msg);
	    }

	}
}
