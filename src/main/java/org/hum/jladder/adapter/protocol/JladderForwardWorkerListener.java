package org.hum.jladder.adapter.protocol;

public class JladderForwardWorkerListener {

	private JladderMessageReceiveEvent eventCallback;

	public void onReceive(JladderMessageReceiveEvent event) {
		this.eventCallback = event;
	}

	public void fireReadEvent(JladderByteBuf jladderByteBuf) {
		eventCallback.onReceive(jladderByteBuf);
	}
}
