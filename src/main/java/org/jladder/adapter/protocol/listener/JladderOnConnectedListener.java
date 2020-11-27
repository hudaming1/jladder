package org.jladder.adapter.protocol.listener;

import org.jladder.adapter.protocol.JladderChannelFuture;
import org.jladder.adapter.protocol.JladderConnectEvent;

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
}
