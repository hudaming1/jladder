package org.jladder.adapter.protocol.listener;

import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderMessageReceiveEvent;

public class JladderOnReceiveDataListener {

	private JladderMessageReceiveEvent eventCallback;

	public void onReceive(JladderMessageReceiveEvent event) {
		this.eventCallback = event;
	}

	public void fireReadEvent(JladderByteBuf jladderByteBuf) {
		eventCallback.onReceive(jladderByteBuf);
	}
}
