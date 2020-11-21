package org.hum.jladder.adapter.protocol.listener;

import org.hum.jladder.adapter.protocol.JladderChannelFuture;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public abstract class JladderConnectListener implements ChannelFutureListener {

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		onConnect(new JladderChannelFuture(future));
	}
	
	public abstract void onConnect(JladderChannelFuture future);
}
