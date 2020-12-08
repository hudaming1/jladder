package org.jladder.adapter.protocol.crypto;

public class CryptoFactory {
	
	private static volatile CryptoFactory instance;
	private Crypto crypto;
	
	private CryptoFactory() {
		this.crypto = new AesCrypto();
	} 
	
	public static CryptoFactory get() {
		if (instance != null) {
			return instance;
		}
		synchronized (instance) {
			if (instance == null) {
				instance = new CryptoFactory();
			}
		}
		return instance;
	}

	public byte[] encrypt(byte[] bytes) {
		return crypto.encrypt(bytes);
	}

	public byte[] decrypt(byte[] bytes) {
		return crypto.decrypt(bytes);
	}
}
