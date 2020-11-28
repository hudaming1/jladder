package org.jladder.adapter.protocol.listener;

public interface JladderAsynForwardClientListener {

	public void onConnect();
	
	public void onDisconnect();
	
	public void onReceiveData();
}
