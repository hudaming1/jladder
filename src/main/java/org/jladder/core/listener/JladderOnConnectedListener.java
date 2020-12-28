package org.jladder.core.listener;

import org.jladder.core.JladderChannelFuture;

public class JladderOnConnectedListener {

	private JladderConnectEvent event;

	public void onConnect(JladderConnectEvent event) {
		this.event = event;
	}

	public void fireReadEvent(JladderChannelFuture channelFuture) {
		if (event != null) {
			event.onConnect(channelFuture);
		}
	}

	public static interface JladderConnectEvent {
		public void onConnect(JladderChannelFuture channelFuture);
	}
}
