package org.jladder.adapter.protocol.listener;

import org.jladder.adapter.protocol.JladderChannelHandlerContext;

public class JladderOnDisconnectedListener {

	private JladderDisconnectEvent event;
	
	public void onDisconnect(JladderDisconnectEvent event) {
		this.event = event;
	}

	public void fireReadEvent(JladderChannelHandlerContext ctx) {
		if (event != null) {
			event.onDisconnect(ctx);
		}
	}

	public static interface JladderDisconnectEvent {
		public void onDisconnect(JladderChannelHandlerContext ctx);
	}
}
