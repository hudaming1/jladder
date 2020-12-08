package org.jladder.adapter.protocol;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jladder.adapter.protocol.enumtype.JladderForwardWorkerStatusEnum;
import org.jladder.adapter.protocol.listener.JladderAsynForwardClientListener;
import org.jladder.adapter.protocol.listener.JladderForwardListener;
import org.jladder.common.exception.JladderException;

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
	private final Lock connectConcurrencyLock = new ReentrantLock();
	private volatile JladderForwardWorkerStatusEnum status = JladderForwardWorkerStatusEnum.Terminated;
	private JladderForwardListener onReceiveListener = new JladderForwardListener();
	private CountDownLatch connectFinishLatch = new CountDownLatch(1);
	private JladderAsynForwardClientInvokeChain jladderAsynForwardClientInvokeChain = new JladderAsynForwardClientInvokeChain();
	
	public JladderAsynForwardClient(String remoteHost, int remotePort, EventLoopGroup eventLoopGroup) {
		this(remoteHost, remotePort, eventLoopGroup, null);
	}
	
	public JladderAsynForwardClient(String remoteHost, int remotePort, EventLoopGroup eventLoopGroup, JladderAsynForwardClientListener listener) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.eventLoopGroup = eventLoopGroup;
		this.initListener(listener);
	}
	
	private void initListener(JladderAsynForwardClientListener listener) {
		jladderAsynForwardClientInvokeChain.addListener(listener);
	}

	public synchronized void connect() throws InterruptedException {
		if (!isCanBeStart()) {
			return ;
		}
		try {
//			connectConcurrencyLock.lock();
//			status = JladderForwardWorkerStatusEnum.Starting;
			
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
			chanelFuture.addListener(f -> {
				if (f.isSuccess()) {
					this.channel = ((ChannelFuture) f).channel();
					status = JladderForwardWorkerStatusEnum.Running;
					jladderAsynForwardClientInvokeChain.onConnect(new JladderChannelFuture((ChannelFuture) f));
				}
				connectFinishLatch.countDown();
			});

			connectFinishLatch.await();
		} finally {
//			connectConcurrencyLock.unlock();
		}
	}
	
	private boolean isCanBeStart() {
		return status != JladderForwardWorkerStatusEnum.Running && status != JladderForwardWorkerStatusEnum.Starting;
	}

	public JladderForwardListener writeAndFlush(ByteBuf message) throws InterruptedException {
		connect();
		if (channel == null) {
			log.error(remoteHost + ":" + remotePort + " uninit...");
			throw new JladderException(remoteHost + ":" + remotePort + " connect failed");
		}
		this.channel.writeAndFlush(message).addListener(f -> {
			// TODO
			if (!f.isSuccess()) {
				log.error(this.channel.toString() + " flush error", f.cause());
			}
//			log.info("flush request=" + message.toString(CharsetUtil.UTF_8));
		});
		
		return onReceiveListener;
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
//		log.info(ctx.channel().toString() + " read " + byteBuf.toString(CharsetUtil.UTF_8));
//		onReceiveListener.fireReadEvent(new JladderByteBuf(byteBuf));
		jladderAsynForwardClientInvokeChain.onReceiveData(new JladderByteBuf(byteBuf));
        ctx.fireChannelRead(msg);
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	log.info(this.channel.toString() + " diconnect");
    	jladderAsynForwardClientInvokeChain.onDisconnect(new JladderChannelHandlerContext(ctx));
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	log.error("remoteHost=" + remoteHost + ":" + remotePort + "[" + ctx.channel().toString() + "]" + " error, ", cause);
    }

	public void addListener(JladderAsynForwardClientListener listener) {
		jladderAsynForwardClientInvokeChain.addListener(listener);
	}
	
	private static class JladderAsynForwardClientInvokeChain implements JladderAsynForwardClientListener {
		private List<JladderAsynForwardClientListener> headerListener = new CopyOnWriteArrayList<JladderAsynForwardClientListener>();

		@Override
		public void onConnect(JladderChannelFuture jladderChannelFuture) {
			headerListener.forEach(listener -> {
				listener.onConnect(jladderChannelFuture);
			});
		}

		@Override
		public void onReceiveData(JladderByteBuf jladderByteBuf) {
			headerListener.forEach(listener -> {
				listener.onReceiveData(jladderByteBuf);
			});
		}

		@Override
		public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
			headerListener.forEach(listener -> {
				listener.onDisconnect(jladderChannelHandlerContext);
			});
		}
		
		public void addListener(JladderAsynForwardClientListener listener) {
			if (listener == null) {
				return ;
			}
			this.headerListener.add(listener);
		}
	}
}