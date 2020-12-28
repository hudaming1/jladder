package org.jladder.core;

import java.net.SocketAddress;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;

public class JladderChannelHandlerContext {

	private ChannelHandlerContext channelHandlerContext;

	public JladderChannelHandlerContext(ChannelHandlerContext ctx) {
		this.channelHandlerContext = ctx;
	}

	public ChannelFuture bind(SocketAddress localAddress) {
		return channelHandlerContext.bind(localAddress);
	}

	public ChannelFuture connect(SocketAddress remoteAddress) {
		return channelHandlerContext.connect(remoteAddress);
	}

	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
		return channelHandlerContext.connect(remoteAddress, localAddress);
	}

	public ChannelFuture disconnect() {
		return channelHandlerContext.disconnect();
	}

	public ChannelFuture close() {
		return channelHandlerContext.close();
	}

	public ChannelFuture deregister() {
		return channelHandlerContext.deregister();
	}

	public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		return channelHandlerContext.bind(localAddress, promise);
	}

	public Channel channel() {
		return channelHandlerContext.channel();
	}

	public EventExecutor executor() {
		return channelHandlerContext.executor();
	}

	public String name() {
		return channelHandlerContext.name();
	}

	public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
		return channelHandlerContext.connect(remoteAddress, promise);
	}

	public ChannelHandler handler() {
		return channelHandlerContext.handler();
	}

	public boolean isRemoved() {
		return channelHandlerContext.isRemoved();
	}

	public ChannelHandlerContext fireChannelRegistered() {
		return channelHandlerContext.fireChannelRegistered();
	}

	public ChannelHandlerContext fireChannelUnregistered() {
		return channelHandlerContext.fireChannelUnregistered();
	}

	public ChannelHandlerContext fireChannelActive() {
		return channelHandlerContext.fireChannelActive();
	}

	public ChannelHandlerContext fireChannelInactive() {
		return channelHandlerContext.fireChannelInactive();
	}

	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
		return channelHandlerContext.connect(remoteAddress, localAddress, promise);
	}

	public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
		return channelHandlerContext.fireExceptionCaught(cause);
	}

	public ChannelHandlerContext fireUserEventTriggered(Object evt) {
		return channelHandlerContext.fireUserEventTriggered(evt);
	}

	public ChannelHandlerContext fireChannelRead(Object msg) {
		return channelHandlerContext.fireChannelRead(msg);
	}

	public ChannelHandlerContext fireChannelReadComplete() {
		return channelHandlerContext.fireChannelReadComplete();
	}

	public ChannelHandlerContext fireChannelWritabilityChanged() {
		return channelHandlerContext.fireChannelWritabilityChanged();
	}

	public ChannelHandlerContext read() {
		return channelHandlerContext.read();
	}

	public ChannelHandlerContext flush() {
		return channelHandlerContext.flush();
	}

	public ChannelPipeline pipeline() {
		return channelHandlerContext.pipeline();
	}

	public ByteBufAllocator alloc() {
		return channelHandlerContext.alloc();
	}

	public ChannelFuture disconnect(ChannelPromise promise) {
		return channelHandlerContext.disconnect(promise);
	}

	@SuppressWarnings("deprecation")
	public <T> Attribute<T> attr(AttributeKey<T> key) {
		return channelHandlerContext.attr(key);
	}

	@SuppressWarnings("deprecation")
	public <T> boolean hasAttr(AttributeKey<T> key) {
		return channelHandlerContext.hasAttr(key);
	}

	public ChannelFuture close(ChannelPromise promise) {
		return channelHandlerContext.close(promise);
	}

	public ChannelFuture deregister(ChannelPromise promise) {
		return channelHandlerContext.deregister(promise);
	}

	public ChannelFuture write(Object msg) {
		return channelHandlerContext.write(msg);
	}

	public ChannelFuture write(Object msg, ChannelPromise promise) {
		return channelHandlerContext.write(msg, promise);
	}

	public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
		return channelHandlerContext.writeAndFlush(msg, promise);
	}

	public ChannelFuture writeAndFlush(Object msg) {
		return channelHandlerContext.writeAndFlush(msg);
	}

	public ChannelPromise newPromise() {
		return channelHandlerContext.newPromise();
	}

	public ChannelProgressivePromise newProgressivePromise() {
		return channelHandlerContext.newProgressivePromise();
	}

	public ChannelFuture newSucceededFuture() {
		return channelHandlerContext.newSucceededFuture();
	}

	public ChannelFuture newFailedFuture(Throwable cause) {
		return channelHandlerContext.newFailedFuture(cause);
	}

	public ChannelPromise voidPromise() {
		return channelHandlerContext.voidPromise();
	}
}
