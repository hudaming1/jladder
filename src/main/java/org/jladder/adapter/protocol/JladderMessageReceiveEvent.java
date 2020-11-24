package org.jladder.adapter.protocol;

public interface JladderMessageReceiveEvent {

	public void onReceive(JladderByteBuf byteBuf);
}
