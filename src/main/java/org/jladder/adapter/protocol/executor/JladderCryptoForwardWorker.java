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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JladderCryptoForwardWorker extends SimpleChannelInboundHandler<JladderMessage> {
	
	private final static Map<String, JladderForwardListener> listenerMap = new ConcurrentHashMap<>();
	private final Bootstrap bootstrap = new Bootstrap();
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
	private volatile boolean isInitHandler = false;
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
				if (isInitHandler) {
					return ;
				}
				ch.pipeline().addLast(new IdleStateHandler(6, 2, 3, TimeUnit.SECONDS));
				ch.pipeline().addLast(new JladderCryptoInHandler());
				ch.pipeline().addLast(new JladderCryptoOutHandler());
				ch.pipeline().addLast(JladderCryptoForwardWorker.this);
				isInitHandler = true;
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
		} else {
			log.error("unsupport message found=" + msg.getMessageType());
		}
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	status = JladderForwardWorkerStatusEnum.Terminated;
    	log.info("outside disconnect");
    	
    	final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                System.err.println("服务端链接不上，开始重连操作...");
                connect().onConnect(f -> {
                	if (!f.isSuccess()) {
                		channelInactive(ctx);
                	} else {
                		System.out.println("重连成功");
                	}
                });
            }
        }, 5L, TimeUnit.SECONDS);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
            	//可以选择重新连接
                System.out.println("长期没收到服务器推送数据");
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("长期未向服务器发送数据");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("ALL");
            }
        }
    }
}
