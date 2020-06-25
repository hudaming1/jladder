package org.hum.nettyproxy.test.https_client.ca.bouncycastle._1;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;

import org.junit.Test;

public class CAManagerTest {
	PEMFileStore<KeyPair> keystore = new PEMFileStore<KeyPair>("D:\\certs\\ca.key");

	PEMFileStore<X509Certificate> certstore = new PEMFileStore<X509Certificate>("D:\\certs\\ca.crt");

	CAManager ca = null;

	/**
	 * Step 1，创建CA
	 * 
	 * @throws KeyPairException
	 * @throws CertificateException
	 */
	public void initCA() throws KeyPairException, CertificateException {
		ca = new CAManager();
		X509Attrs principals = new X509Attrs();
		principals.setCommonName("私享家CA根证书");
		principals.setCountryCode("AU");
		ca.init(keystore, certstore, principals);
	}

	@Test
	public void testProcess() throws KeyPairException, CertificateException, StorageException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, java.security.cert.CertificateException, IOException {
		initCA();
	}
}
