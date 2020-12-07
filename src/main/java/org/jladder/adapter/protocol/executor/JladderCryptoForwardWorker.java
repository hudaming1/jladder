package org.jladder.adapter.protocol.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.enumtype.JladderForwardWorkerStatusEnum;
import org.jladder.adapter.protocol.listener.JladderOnConnectedListener;
import org.jladder.adapter.protocol.message.JladderMessage;
import org.jladder.adapter.protocol.listener.JladderForwardListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JladderCryptoForwardWorker extends SimpleChannelInboundHandler<JladderMessage> {
	
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
	private EventLoopGroup eventLoopGroup;
	private Channel channel;
	private String proxyHost;
	private int proxyPort;
	private Map<String, JladderForwardListener> listenerMap = new ConcurrentHashMap<>();
	
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

	public JladderForwardListener writeAndFlush(JladderMessage message) {
		if (status != JladderForwardWorkerStatusEnum.Running) {
			throw new IllegalStateException("channel not connect or has closed.");
		}

		listenerMap.put(message.getClientIden(), new JladderForwardListener());
		
		if (message.getBody() != null) {
			message.getBody().retain();
		}
		
		this.channel.writeAndFlush(message).addListener(f -> {
			// TODO
			// sign writable
			log.info("http2.executor flushed");
		});
		
		return listenerMap.get(message.getClientIden());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JladderMessage msg) throws Exception {
		log.info(msg.getClientIden() + " receive outside message");
		listenerMap.get(msg.getClientIden()).fireReadEvent(new JladderByteBuf(msg.getBody()));
        ctx.fireChannelRead(msg);
	}
}
