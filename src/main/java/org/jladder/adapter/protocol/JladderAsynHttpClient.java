package org.jladder.adapter.protocol;

import java.util.concurrent.CountDownLatch;

import org.jladder.adapter.protocol.enumtype.JladderForwardWorkerStatusEnum;
import org.jladder.adapter.protocol.listener.JladderOnConnectedListener;
import org.jladder.adapter.protocol.listener.JladderOnReceiveDataListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class JladderAsynHttpClient extends ChannelInboundHandlerAdapter {
	
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
	private EventLoopGroup eventLoopGroup;
	private Channel channel;
	private String remoteHost;
	private int remotePort;
	private JladderOnReceiveDataListener onReceiveListener = new JladderOnReceiveDataListener();
	private JladderOnConnectedListener onConnectedListener = new JladderOnConnectedListener();
	private CountDownLatch connectLatch = new CountDownLatch(1);
	
	public JladderAsynHttpClient(String proxyHost, int proxyPort) {
		this(proxyHost, proxyPort, new NioEventLoopGroup());
	}
	
	public JladderAsynHttpClient(String remoteHost, int remotePort, EventLoopGroup eventLoopGroup) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.eventLoopGroup = eventLoopGroup;
	}

	public JladderOnConnectedListener connect() {
		if (!isCanBeStart()) {
			throw new IllegalStateException("worker cann't be connect, current_status=" + status);
		}
		status = JladderForwardWorkerStatusEnum.Starting;
		
		// init bootstrap
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(eventLoopGroup);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(JladderAsynHttpClient.this);
			}
		});	
		ChannelFuture chanelFuture = bootstrap.connect(remoteHost, remotePort);
		this.channel = chanelFuture.channel();
		chanelFuture.addListener(f -> {
			if (f.isSuccess()) {
				status = JladderForwardWorkerStatusEnum.Running;
			}
			onConnectedListener.fireReadEvent(new JladderChannelFuture((ChannelFuture) f));
		});
		return onConnectedListener;
	}
	
	private boolean isCanBeStart() {
		return status != JladderForwardWorkerStatusEnum.Running && status != JladderForwardWorkerStatusEnum.Starting;
	}

	public JladderOnReceiveDataListener writeAndFlush(ByteBuf message) throws InterruptedException {
		if (status != JladderForwardWorkerStatusEnum.Running) {
			connect().onConnect(new JladderConnectEvent() {
				@Override
				public void onConnect(JladderChannelFuture channelFuture) {
					connectLatch.countDown();
				}
			});
			connectLatch.await();
		}
		
		this.channel.writeAndFlush(message).addListener(f -> {
			// TODO
			// sign writable
		});
		
		return onReceiveListener;
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ByteBuf) {
			onReceiveListener.fireReadEvent(new JladderByteBuf((ByteBuf) msg));
		}
        ctx.fireChannelRead(msg);
	}
}