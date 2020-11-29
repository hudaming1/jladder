package org.jladder.adapter.protocol.listener;

import org.jladder.adapter.protocol.JladderByteBuf;

public class JladderOnReceiveDataListener {

	private JladderMessageReceiveEvent eventCallback;

	public void onReceive(JladderMessageReceiveEvent event) {
		this.eventCallback = event;
	}

	public void fireReadEvent(JladderByteBuf jladderByteBuf) {
		if (eventCallback != null)
			eventCallback.onReceive(jladderByteBuf);
	}

	public static interface JladderMessageReceiveEvent {
		public void onReceive(JladderByteBuf byteBuf);
	}
}
