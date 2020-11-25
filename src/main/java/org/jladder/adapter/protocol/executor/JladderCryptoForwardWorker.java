package org.jladder.adapter.protocol.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderMessage;
import org.jladder.adapter.protocol.enumtype.JladderForwardWorkerStatusEnum;
import org.jladder.adapter.protocol.listener.JladderOnConnectedListener;
import org.jladder.adapter.protocol.listener.JladderOnReceiveDataListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class JladderCryptoForwardWorker extends SimpleChannelInboundHandler<JladderMessage> {
	
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
	private EventLoopGroup eventLoopGroup;
	private Channel channel;
	private String proxyHost;
	private int proxyPort;
	private Map<Long, JladderOnReceiveDataListener> listenerMap = new ConcurrentHashMap<>();
	
	public JladderCryptoForwardWorker(String proxyHost, int proxyPort) {
		this(proxyHost, proxyPort, new NioEventLoopGroup());
	}
	
	public JladderCryptoForwardWorker(String proxyHost, int proxyPort, EventLoopGroup eventLoopGroup) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
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
				ch.pipeline().addLast(new JladderCryptoInHandler());
				ch.pipeline().addLast(new JladderCryptoOutHandler());
				ch.pipeline().addLast(JladderCryptoForwardWorker.this);
			}
		});	
		ChannelFuture chanelFuture = bootstrap.connect(proxyHost, proxyPort);
		this.channel = chanelFuture.channel();
		chanelFuture.addListener(f -> {
			if (f.isSuccess()) {
				status = JladderForwardWorkerStatusEnum.Running;
			}
		});
		return new JladderOnConnectedListener();
	}
	
	private boolean isCanBeStart() {
		return status != JladderForwardWorkerStatusEnum.Running && status != JladderForwardWorkerStatusEnum.Starting;
	}

	public JladderOnReceiveDataListener writeAndFlush(JladderMessage message) {
		if (status != JladderForwardWorkerStatusEnum.Running) {
			throw new IllegalStateException("channel not connect or has closed.");
		}

		listenerMap.put(message.getId(), new JladderOnReceiveDataListener());
		
		if (message.getBody() != null) {
			message.getBody().retain();
		}
		
		this.channel.writeAndFlush(message).addListener(f -> {
			// TODO
			// sign writable
			System.out.println("executor flushed");
		});
		
		return listenerMap.get(message.getId());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JladderMessage msg) throws Exception {
		listenerMap.get(msg.getId()).fireReadEvent(new JladderByteBuf(msg.getBody()));
        ctx.fireChannelRead(msg);
	}
}
