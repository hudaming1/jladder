package org.jladder.adapter.protocol.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

	public JladderOnConnectedListener connect() {
		JladderOnConnectedListener jladderOnConnectedListener = new JladderOnConnectedListener();
		if (!isCanBeStart()) {
			throw new IllegalStateException("worker cann't be connect, current_status=" + status);
		}
		status = JladderForwardWorkerStatusEnum.Starting;
		
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
			// TODO 如果非runing状态，则重连(因为没有heartbeat机制，所以服务端「偷偷」close连接时，客户端无感知，这里需要重连)
			throw new IllegalStateException("channel not connect or has closed.");
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
		log.debug("remove listener, residue listener.count=" + listenerMap.size());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JladderMessage msg) throws Exception {
		if (msg instanceof JladderDataMessage) {
			log.debug("[msg" + msg.getMsgId() + "][" + msg.getClientIden() + "] read message-len=" + ((JladderDataMessage) msg).getBody().readableBytes());
			listenerMap.get(msg.getClientIden()).fireReadEvent(new JladderByteBuf(((JladderDataMessage) msg).getBody()));
		} else if (msg instanceof JladderDisconnectMessage) {
			listenerMap.get(msg.getClientIden()).fireDisconnectEvent((JladderDisconnectMessage) msg);
		} else {
			log.error("unsupport message found=" + msg.getMessageType());
		}
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	status = JladderForwardWorkerStatusEnum.Terminated;
    	log.debug("outside disconnect");
    	
    	final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                log.debug("prepare reconnect....");
                connect().onConnect(f -> {
                	if (!f.isSuccess()) {
                		channelInactive(ctx);
                	} else {
                		log.debug("connected success");
                	}
                });
            }
        }, 5L, TimeUnit.SECONDS);
    }
}
