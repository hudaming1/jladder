package org.jladder.adapter.protocol.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderChannelFuture;
import org.jladder.adapter.protocol.enumtype.JladderForwardWorkerStatusEnum;
import org.jladder.adapter.protocol.listener.JladderForwardListener;
import org.jladder.adapter.protocol.listener.JladderOnConnectedListener;
import org.jladder.adapter.protocol.message.JladderDataMessage;
import org.jladder.adapter.protocol.message.JladderDisconnectMessage;
import org.jladder.adapter.protocol.message.JladderMessage;

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
	
	private final static Map<String, JladderForwardListener> listenerMap = new ConcurrentHashMap<>();
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
	private EventLoopGroup eventLoopGroup;
	private Channel channel;
	private String proxyHost;
	private int proxyPort;
	
	public JladderCryptoForwardWorker(String proxyHost, int proxyPort) {
		this(proxyHost, proxyPort, new NioEventLoopGroup());
	}
	
	public JladderCryptoForwardWorker(String proxyHost, int proxyPort, EventLoopGroup eventLoopGroup) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.eventLoopGroup = eventLoopGroup;
	}

	public JladderOnConnectedListener connect() {
		JladderOnConnectedListener jladderOnConnectedListener = new JladderOnConnectedListener();
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
			} else {
				log.error("connect outside failed", f.cause());
			}
			jladderOnConnectedListener.fireReadEvent(new JladderChannelFuture((ChannelFuture) f));
		});
		return jladderOnConnectedListener;
	}
	
	private boolean isCanBeStart() {
		return status != JladderForwardWorkerStatusEnum.Running && status != JladderForwardWorkerStatusEnum.Starting;
	}

	public JladderForwardListener writeAndFlush(JladderMessage message) {
		if (status != JladderForwardWorkerStatusEnum.Running) {
			throw new IllegalStateException("channel not connect or has closed.");
		}
		
		listenerMap.putIfAbsent(message.getClientIden(), new JladderForwardListener());
		
		this.channel.writeAndFlush(message).addListener(f -> {
			if (!f.isSuccess()) {
				log.error("[{}]flush message error", message.getClientIden(), f.cause());
			} else {
				log.info("[{}]message flushed", message.getClientIden());
			}
		});
		
		return listenerMap.get(message.getClientIden());
	}
	
	// TODO 
	public void removeClientIden(String clientIden) {
		listenerMap.remove(clientIden);
		log.info("remove listener, residue listener.count=" + listenerMap.size());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JladderMessage msg) throws Exception {
		if (msg instanceof JladderDataMessage) {
			listenerMap.get(msg.getClientIden()).fireReadEvent(new JladderByteBuf(((JladderDataMessage) msg).getBody()));
		} else if (msg instanceof JladderDisconnectMessage) {
			listenerMap.get(msg.getClientIden()).fireDisconnectEvent((JladderDisconnectMessage) msg);
//			removeClientIden(msg.getClientIden());
		} else {
			log.error("unsupport message found=" + msg.getMessageType());
		}
	}
}
