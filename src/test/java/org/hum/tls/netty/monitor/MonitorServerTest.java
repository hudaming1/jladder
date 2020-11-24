package org.hum.tls.netty.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hum.jladder.common.core.NettyProxyContext;
import org.hum.jladder.compoment.monitor.NettyProxyMonitorHandler;
import org.hum.jladder.compoment.monitor.NettyProxyMonitorManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class MonitorServerTest {
	
	private static NettyProxyMonitorManager monitor = new NettyProxyMonitorManager();
	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	static {
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("connection-count=" + monitor.getConnectionCount() + ", in-len=" + monitor.getInBytesLength() + ", out-len=" + monitor.getOutBytesLength());
			}
		}, 0, 5, TimeUnit.SECONDS);
	}

	public static void main(String[] args) {
		
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(4));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addFirst(new NettyProxyMonitorHandler());
				ch.pipeline().addLast(new TestInboundChannelHandler());
			}
		});
		serverBootstrap.bind(9999).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				NettyProxyContext.regist(monitor);
				System.out.println("server start, listen port: 9999");
			}
		});
	}
	
	private static class TestInboundChannelHandler extends ChannelInboundHandlerAdapter {

	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	ByteBuf byteBuf = (ByteBuf) msg;
	    	int magincNum = byteBuf.readInt();
	    	System.out.println("magincNum=" + magincNum);
	    	if (magincNum != 52996) {
	    		ctx.close();
	    		System.out.println("magic number error, close connection..");
	    		return ;
	    	}
	    	
	    	byte[] bytes = new byte[byteBuf.readInt()];
	    	byteBuf.readBytes(bytes);
	    	
	    	System.out.println(new String(bytes, "utf-8"));
	    	
	    	ByteBuf outBuf = ctx.alloc().directBuffer();
	    	outBuf.writeInt(51996);
	    	ctx.writeAndFlush(outBuf);
	    	System.out.println("flsuh to client");
	    }
	}
}
