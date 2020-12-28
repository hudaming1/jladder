package org.jladder.core.listener;

import org.jladder.core.JladderByteBuf;
import org.jladder.core.JladderChannelFuture;
import org.jladder.core.JladderChannelHandlerContext;

public interface JladderAsynForwardClientListener {

	public void onConnect(JladderChannelFuture jladderChannelFuture);
	
	public void onReceiveData(JladderByteBuf jladderByteBuf);

	public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext);

}
