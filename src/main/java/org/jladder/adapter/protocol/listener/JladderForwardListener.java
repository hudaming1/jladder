package org.jladder.adapter.protocol.listener;

import org.jladder.adapter.protocol.JladderByteBuf;

public class JladderForwardListener {

	private JladderMessageReceiveEvent eventCallback;

	public JladderForwardListener onReceive(JladderMessageReceiveEvent event) {
		this.eventCallback = event;
		return this;
	}

	public void fireReadEvent(JladderByteBuf jladderByteBuf) {
		if (eventCallback != null)
			eventCallback.onReceive(jladderByteBuf);
	}

	public static interface JladderMessageReceiveEvent {
		public void onReceive(JladderByteBuf byteBuf);
	}
}
