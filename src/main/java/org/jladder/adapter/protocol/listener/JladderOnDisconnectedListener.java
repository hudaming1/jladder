package org.jladder.adapter.protocol.listener;

import org.jladder.adapter.protocol.JladderChannelFuture;

public class JladderOnDisconnectedListener {

	private JladderDisconnectEvent event;

	public void onDisconnect(JladderDisconnectEvent event) {
		this.event = event;
	}

	public void fireReadEvent(JladderChannelFuture channelFuture) {
		if (event != null) {
			event.onConnect(channelFuture);
		}
	}

	public static interface JladderDisconnectEvent {
		public void onConnect(JladderChannelFuture channelFuture);
	}
}
