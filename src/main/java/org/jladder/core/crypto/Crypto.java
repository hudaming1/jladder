package org.jladder.core.crypto;

public interface Crypto {

	public byte[] encrypt(byte[] bytes);
	
	public byte[] decrypt(byte[] bytes);
}
