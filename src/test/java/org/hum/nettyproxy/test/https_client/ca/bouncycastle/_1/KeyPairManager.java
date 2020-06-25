package org.hum.nettyproxy.test.https_client.ca.bouncycastle._1;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

/**
 * @author Administrator 密钥管理器
 *
 */
public class KeyPairManager {
	static {
		// Load BouncyCastle security provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/**
	 * Generates an RSA public/private KeyPair
	 * 
	 * @return Generated KeyPair
	 * @throws KeyPairException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateRSAKeyPair() throws KeyPairException {
		// http://docs.oracle.com/javase/1.5.0/docs/guide/security/CryptoSpec.html#AppA
		try {
			KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
			kpGen.initialize(1024, new SecureRandom());
			return kpGen.generateKeyPair();
		} catch (Exception e) {
			throw new KeyPairException(e);
		}
	}
}
