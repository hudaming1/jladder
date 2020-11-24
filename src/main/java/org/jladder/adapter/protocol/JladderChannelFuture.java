package org.jladder.adapter.protocol;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
@SuppressWarnings("unchecked")
public class JladderChannelFuture {

	private ChannelFuture future;
	
	public JladderChannelFuture(ChannelFuture future) {
		this.future = future;
	}

	public boolean isSuccess() {
		return future.isSuccess();
	}

	public boolean isCancellable() {
		return future.isCancellable();
	}

	public Throwable cause() {
		return future.cause();
	}

	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return future.await(timeout, unit);
	}

	public boolean isCancelled() {
		return future.isCancelled();
	}

	public boolean isDone() {
		return future.isDone();
	}

	public boolean await(long timeoutMillis) throws InterruptedException {
		return future.await(timeoutMillis);
	}

	public Void get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		return future.awaitUninterruptibly(timeout, unit);
	}

	public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}

	public boolean awaitUninterruptibly(long timeoutMillis) {
		return future.awaitUninterruptibly(timeoutMillis);
	}

	public Void getNow() {
		return future.getNow();
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	public Channel channel() {
		return future.channel();
	}

	public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
		return future.addListener(listener);
	}

	public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
		return future.addListeners(listeners);
	}

	public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
		return future.removeListener(listener);
	}

	public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
		return future.removeListeners(listeners);
	}

	public ChannelFuture sync() throws InterruptedException {
		return future.sync();
	}

	public ChannelFuture syncUninterruptibly() {
		return future.syncUninterruptibly();
	}

	public ChannelFuture await() throws InterruptedException {
		return future.await();
	}

	public ChannelFuture awaitUninterruptibly() {
		return future.awaitUninterruptibly();
	}

	public boolean isVoid() {
		return future.isVoid();
	}

	public void writeAndFlush(JladderMessage message) {
		future.channel().writeAndFlush(message);
	}
	
}
