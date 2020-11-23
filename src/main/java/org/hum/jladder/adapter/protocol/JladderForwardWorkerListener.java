package org.hum.jladder.adapter.protocol;

import org.hum.jladder.adapter.protocol.listener.JladderReadListener;

public class JladderForwardWorkerListener {
	
	private JladderForwardWorker jladderForwardWorker;
	
	public JladderForwardWorkerListener(JladderForwardWorker jladderForwardWorker) {
		this.jladderForwardWorker = jladderForwardWorker;
	}
	
	public void onReceive(JladderMessageReceiveEvent event) {
		jladderForwardWorker.onRead(new JladderReadListener() {
			@Override
			public void onRead(JladderByteBuf msg) {
				event.onReceive(msg);
			}
		});
	}
}
