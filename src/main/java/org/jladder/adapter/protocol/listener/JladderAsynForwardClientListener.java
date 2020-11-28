package org.jladder.adapter.protocol.listener;

import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderChannelFuture;
import org.jladder.adapter.protocol.JladderChannelHandlerContext;

public interface JladderAsynForwardClientListener {

	public void onConnect(JladderChannelFuture jladderChannelFuture);
	
	public void onReceiveData(JladderByteBuf jladderByteBuf);

	public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext);

}
