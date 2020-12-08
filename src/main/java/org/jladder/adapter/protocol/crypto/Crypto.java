package org.jladder.adapter.protocol.crypto;

public interface Crypto {

	public byte[] encrypt(byte[] bytes);
	
	public byte[] decrypt(byte[] bytes);
}
