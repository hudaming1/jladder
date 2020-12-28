package org.jladder.core.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jladder.core.JladderByteBuf;
import org.jladder.core.JladderChannelFuture;
import org.jladder.core.enumtype.JladderForwardWorkerStatusEnum;
import org.jladder.core.listener.JladderForwardListener;
import org.jladder.core.listener.JladderOnConnectedListener;
import org.jladder.core.message.JladderDataMessage;
import org.jladder.core.message.JladderDisconnectMessage;
import org.jladder.core.message.JladderMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class JladderCryptoForwardWorker extends SimpleChannelInboundHandler<JladderMessage> {
	
	private final static Map<String, JladderForwardListener> listenerMap = new ConcurrentHashMap<>();
	private final Bootstrap bootstrap = new Bootstrap();
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
	private EventLoopGroup eventLoopGroup;
	private Channel channel;
	private String proxyHost;
	private int proxyPort;
	
	public JladderCryptoForwardWorker(String proxyHost, int proxyPort, EventLoopGroup eventLoopGroup) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.eventLoopGroup = eventLoopGroup;
		initBootStrap();
	}
	
	private void initBootStrap() {
		// init bootstrap
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
	}

	public synchronized JladderOnConnectedListener connect() {
		JladderOnConnectedListener jladderOnConnectedListener = new JladderOnConnectedListener();
		if (status == JladderForwardWorkerStatusEnum.Running) {
			throw new IllegalStateException("worker cann't be connect, current_status=" + status);
		}
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
	
	private void ensureConnected() {
		if (status == JladderForwardWorkerStatusEnum.Running) {
			return ;
		}
		CountDownLatch latch = new CountDownLatch(1);
		try {
			connect().onConnect(event -> {
				latch.countDown();
			});
			latch.await();
		} catch (Exception e) {
			log.error("connect out-side error", e);
		}
	}

	public JladderForwardListener writeAndFlush(JladderMessage message) {
		if (status != JladderForwardWorkerStatusEnum.Running) {
			ensureConnected();
		}
		
		listenerMap.putIfAbsent(message.getClientIden(), new JladderForwardListener());
		
		this.channel.writeAndFlush(message).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
            	if (!f.isSuccess()) {
    				log.error("[{}]flush message error", message.getClientIden(), f.cause());
    			} else {
    				log.debug("[{}]message flushed2", message.getClientIden());
    			}
            }
		});
		log.debug("[{}]message flushed", message.getClientIden());
		
		return listenerMap.get(message.getClientIden());
	}
	
	public void removeClientIden(String clientIden) {
		listenerMap.remove(clientIden);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JladderMessage msg) throws Exception {
		if (msg instanceof JladderDataMessage) {
			if (listenerMap.containsKey(msg.getClientIden())) {
				log.debug("[msg" + msg.getMsgId() + "][" + msg.getClientIden() + "] read message-len=" + ((JladderDataMessage) msg).getBody().readableBytes());
				listenerMap.get(msg.getClientIden()).fireReadEvent(new JladderByteBuf(((JladderDataMessage) msg).getBody()));
			}
		} else if (msg instanceof JladderDisconnectMessage) {
			if (listenerMap.containsKey(msg.getClientIden())) {
				listenerMap.get(msg.getClientIden()).fireDisconnectEvent((JladderDisconnectMessage) msg);
			}
		} else {
			log.error("unsupport message found=" + msg.getMessageType());
		}
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	status = JladderForwardWorkerStatusEnum.Terminated;
    	log.warn("outside disconnect");
    	
    	final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("prepare reconnect....");
                connect().onConnect(f -> {
                	if (!f.isSuccess()) {
                		channelInactive(ctx);
                	} else {
                		log.info("connected success");
                	}
                });
            }
        }, 5L, TimeUnit.SECONDS);
    }
}
