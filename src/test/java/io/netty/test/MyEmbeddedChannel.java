package io.netty.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.ReferenceCountUtil;

public class MyEmbeddedChannel extends EmbeddedChannel {

	public MyEmbeddedChannel(ChannelHandler... handlers) {
		super(handlers);
		try {
			// message-queue
			Field queueMessageField = EmbeddedChannel.class.getDeclaredField("outboundMessages");
			queueMessageField.setAccessible(true);
			if (queueMessageField.get(this) == null) {
				queueMessageField.set(this, new ArrayBlockingQueue<Object>(1024));
			}
			queueMessageField.setInt(this, 1);
			System.out.println(queueMessageField.getModifiers());
			// eventloop
			Field eventLoopGroupField = EmbeddedChannel.class.getDeclaredField("loop");
			eventLoopGroupField.setAccessible(true);
			eventLoopGroupField.setInt(this, 2);
//			eventLoopGroupField.set(eventLoopGroupField, 2);
			eventLoopGroupField.set(this, new NioEventLoopGroup(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public Object readOutbound() {
		try {
			Field outboundMessages = EmbeddedChannel.class.getDeclaredField("outboundMessages");
			outboundMessages.setAccessible(true);
			ArrayBlockingQueue<Object> queue = (ArrayBlockingQueue<Object>) outboundMessages.get(this);
			Object message = queue.take();
			if (message != null) {
				ReferenceCountUtil.touch(message, "Caller of readOutbound() will handle the message from this point.");
			}
			return message;
		} catch (Exception e) {
			throw new RuntimeException("readOutbound occured exception.", e);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ArrayBlockingQueue<Object> arrayBlockingQueue = new ArrayBlockingQueue<Object>(1024);
		arrayBlockingQueue.take();
		System.out.println(11);
	}
}
