package org.jladder.adapter.protocol;

import org.jladder.adapter.protocol.listener.JladderAsynForwardClientListener;

public  class SimpleJladderAsynForwardClientListener implements JladderAsynForwardClientListener {
	
	private JladderAsynForwardClientListener next;
	
	public SimpleJladderAsynForwardClientListener(JladderAsynForwardClientListener next) {
		this.next = next;
	}

	@Override
	public void onConnect(JladderChannelFuture jladderChannelFuture) {
		if (onConnect0(jladderChannelFuture)) {
			if (this.next != null) {
				this.next.onConnect(jladderChannelFuture);
			}
		}
	}
	
	public boolean onConnect0(JladderChannelFuture jladderChannelFuture) {
		return true;
	}

	@Override
	public void onReceiveData(JladderByteBuf jladderByteBuf) {
		if (onReceiveData0(jladderByteBuf)) {
			if (this.next != null) {
				this.next.onReceiveData(jladderByteBuf);
			}
		}
	}
	
	public boolean onReceiveData0(JladderByteBuf jladderByteBuf) {
		return true;
	}

	@Override
	public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
		if (onDisconnect0(jladderChannelHandlerContext)) {
			if (this.next != null) {
				this.next.onDisconnect(jladderChannelHandlerContext);
			}
		}
	}
	
	public boolean onDisconnect0(JladderChannelHandlerContext jladderChannelHandlerContext) {
		return true;
	}
}
