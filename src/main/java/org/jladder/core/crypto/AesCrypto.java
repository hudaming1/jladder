package org.jladder.core.crypto;

import org.jladder.common.util.AESCoder;

public class AesCrypto implements Crypto {

	@Override
	public byte[] encrypt(byte[] bytes) {
		return AESCoder.encrypt(bytes);
	}

	@Override
	public byte[] decrypt(byte[] bytes) {
		return AESCoder.decrypt(bytes);
	}
}
