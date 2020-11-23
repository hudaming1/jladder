package org.hum.jladder.adapter.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hum.jladder.adapter.protocol.listener.JladderConnectListener;
import org.hum.jladder.adapter.protocol.listener.JladderReadListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class JladderForwardWorker extends ChannelDuplexHandler {
	
	private EventLoopGroup eventLoopGroup;
	private JladderConnectListener jladderConnectListener;
	private JladderReadListener jladderReadListener;
	private Channel channel;
	private Bootstrap bootstrap;
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
		bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(eventLoopGroup);
		// NettyBootstrapUtil.initTcpServerOptions(bootstrap, Config);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new JladderCodecHandler());
			}
		});	
		ChannelFuture chanelFuture = bootstrap.connect(proxyHost, proxyPort);
		this.channel = chanelFuture.channel();
		chanelFuture.addListener(jladderConnectListener);
		return this;
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof ByteBuf) {
    		long msgId = parseId(msg);
    		// TODO 
    		listenerMap.get(msgId).onReceive(event);
    		jladderReadListener.onRead(new JladderByteBuf((ByteBuf) msg));
    	}
        ctx.fireChannelRead(msg);
    }

	private long parseId(Object msg) {
		// TODO Auto-generated method stub
		return 0;
	}

	public JladderForwardWorker onRead(JladderReadListener jladderReadListener) {
		this.jladderReadListener = jladderReadListener;
		return this;
	}

	public JladderForwardWorker onConnect(JladderConnectListener jladderConnectListener) {
		this.jladderConnectListener = jladderConnectListener;
		return this;
	}
	
	public JladderForwardWorkerListener writeAndFlush(JladderMessage message) {
		if (this.channel == null || !this.channel.isActive()) {
			throw new IllegalStateException("channel not connect or has closed.");
		}

		message.setId(System.nanoTime());
		
		listenerMap.put(message.getId(), new JladderForwardWorkerListener(this));
		
		this.channel.writeAndFlush(message).addListener(f -> {
			// TODO
			// sign writable
		});
		return listenerMap.get(message.getId());
	}
}
