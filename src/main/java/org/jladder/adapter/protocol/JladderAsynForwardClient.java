package org.jladder.adapter.protocol;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jladder.adapter.protocol.enumtype.JladderForwardWorkerStatusEnum;
import org.jladder.adapter.protocol.listener.JladderAsynForwardClientListener;
import org.jladder.adapter.protocol.listener.JladderOnConnectedListener;
import org.jladder.adapter.protocol.listener.JladderOnDisconnectedListener;
import org.jladder.adapter.protocol.listener.JladderOnReceiveDataListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class JladderAsynForwardClient extends ChannelInboundHandlerAdapter {
	
	private EventLoopGroup eventLoopGroup;
	private Channel channel;
	private String remoteHost;
	private int remotePort;
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
//	private JladderOnReceiveDataListener onReceiveListener;
//	private JladderOnConnectedListener onConnectedListener = new JladderOnConnectedListener();
//	private JladderOnDisconnectedListener onDisconnectListener = null;
	private JladderAsynForwardClientListener listener;
	private CountDownLatch connectLatch = new CountDownLatch(1);
	private CountDownLatch connectStartLatch = new CountDownLatch(1);
	
	public JladderAsynForwardClient(String remoteHost, int remotePort, EventLoopGroup eventLoopGroup) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.eventLoopGroup = eventLoopGroup;
	}

	public JladderOnConnectedListener connect() {
		if (!isCanBeStart()) {
			return null;
		}
		status = JladderForwardWorkerStatusEnum.Starting;
		
		// init bootstrap
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(eventLoopGroup);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(JladderAsynForwardClient.this);
			}
		});	
		ChannelFuture chanelFuture = bootstrap.connect(remoteHost, remotePort);
		this.channel = chanelFuture.channel();
		connectStartLatch.countDown();
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

	public JladderAsynForwardClient writeAndFlush(ByteBuf message) throws InterruptedException {
		if (status != JladderForwardWorkerStatusEnum.Running) {
			_connect();
		}
		
		this.channel.writeAndFlush(message).addListener(f -> {
			// TODO
			if (!f.isSuccess()) {
				f.cause().printStackTrace();
			}
			// sign writable
		});
		
		return this;
	}

	private Lock lock = new ReentrantLock();
	// 确保只有一个线程建立连接
	private void _connect() {
		lock.lock();
		try {
			if (isCanBeStart()) {
				connect().onConnect(f -> {
					connectLatch.countDown();
				});
				connectLatch.await();
			}
		} catch (InterruptedException e) {
			log.error("", e);
		} finally {
			lock.unlock();
		}
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ByteBuf) {
			onReceiveListener.fireReadEvent(new JladderByteBuf((ByteBuf) msg));
		}
        ctx.fireChannelRead(msg);
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	if (this.listener != null) {
    		this.listener.fireReadEvent(new JladderChannelHandlerContext(ctx));
    	}
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	log.error("remoteHost=" + remoteHost + ":" + remotePort + " error, ", cause);
    }

	public void addListener(JladderAsynForwardClientListener listener) {
		this.listener = listener;
	}
}