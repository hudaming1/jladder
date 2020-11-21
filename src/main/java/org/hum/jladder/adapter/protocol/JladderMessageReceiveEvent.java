package org.hum.jladder.adapter.protocol;

public interface JladderMessageReceiveEvent {

	public void onReceive(JladderByteBuf byteBuf);
}
