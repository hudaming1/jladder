package org.hum.jladder.adapter.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hum.jladder.adapter.protocol.enumtype.JladderForwardWorkerStatusEnum;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class JladderForwardWorker extends ChannelDuplexHandler {
	
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
	private EventLoopGroup eventLoopGroup;
	private Channel channel;
	private String proxyHost;
	private int proxyPort;
	private Map<Long, JladderForwardWorkerListener> listenerMap = new ConcurrentHashMap<>();
	
	public JladderForwardWorker(String proxyHost, int proxyPort) {
		this(proxyHost, proxyPort, new NioEventLoopGroup());
	}
	
	public JladderForwardWorker(String proxyHost, int proxyPort, EventLoopGroup eventLoopGroup) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.eventLoopGroup = eventLoopGroup;
	}

	public JladderForwardWorker connect() {
		if (!isCanBeStart()) {
			return this;
		}
		status = JladderForwardWorkerStatusEnum.Starting;
		
		// init bootstrap
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(eventLoopGroup);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new JladderCodecHandler());
			}
		});	
		ChannelFuture chanelFuture = bootstrap.connect(proxyHost, proxyPort);
		this.channel = chanelFuture.channel();
		chanelFuture.addListener(f -> {
			if (f.isSuccess()) {
				status = JladderForwardWorkerStatusEnum.Running;
			}
		});
		return this;
	}
	
	private boolean isCanBeStart() {
		return status != JladderForwardWorkerStatusEnum.Running && status != JladderForwardWorkerStatusEnum.Starting;
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof JladderMessage) {
    		JladderMessage jladderByteBuf = (JladderMessage) msg;
    		listenerMap.get(jladderByteBuf.getId()).fireReadEvent(new JladderByteBuf(jladderByteBuf.getBody()));
    	}
        ctx.fireChannelRead(msg);
    }

	public JladderForwardWorkerListener writeAndFlush(JladderMessage message) {
		if (status != JladderForwardWorkerStatusEnum.Running) {
			throw new IllegalStateException("channel not connect or has closed.");
		}

		message.setId(System.nanoTime());
		
		listenerMap.put(message.getId(), new JladderForwardWorkerListener());
		
		this.channel.writeAndFlush(message).addListener(f -> {
			// TODO
			// sign writable
		});
		
		return listenerMap.get(message.getId());
	}
}
