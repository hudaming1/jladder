package org.hum.nettyproxy.test.https_client.certs;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;

import sun.security.x509.X509CertImpl;

public class ReadCert {

	public static void main(String[] args) {
		read("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/dynamic/huming_test2.p12", "123456");
		System.out.println();
		System.out.println("===================");
		System.out.println();
		read("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/server/baidu_server.p12", "123456");
	}

	public static void read(String KEYSTORE_FILE, String KEYSTORE_PASSWORD) {

		try {

			KeyStore ks = KeyStore.getInstance("PKCS12");

			FileInputStream fis = new FileInputStream(KEYSTORE_FILE);

			char[] nPassword = null;

			if ((KEYSTORE_PASSWORD == null) || KEYSTORE_PASSWORD.trim().equals("")) {
				nPassword = null;
			} else {
				nPassword = KEYSTORE_PASSWORD.toCharArray();
			}

			ks.load(fis, nPassword);

			fis.close();

			System.out.println("keystore type=" + ks.getType());

			Enumeration enum1 = ks.aliases();

			String keyAlias = null;

			if (enum1.hasMoreElements()) {
				keyAlias = (String) enum1.nextElement();
				System.out.println("alias=[" + keyAlias + "]");
			}

			PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);

			sun.security.x509.X509CertImpl cert = (X509CertImpl) ks.getCertificate(keyAlias);

			PublicKey pubkey = cert.getPublicKey();
			
			Enumeration<String> elements = cert.getElements();
			
			while (elements.hasMoreElements()) {
				String key = elements.nextElement();
				if (cert.get(key) instanceof sun.security.x509.X509CertInfo) {
					sun.security.x509.X509CertInfo x509 = (sun.security.x509.X509CertInfo)cert.get(key);
					Enumeration<String> elements2 = x509.getElements();
					System.out.println("start=============");
					while (elements2.hasMoreElements()) {
						String nextElement = elements2.nextElement();
						System.out.println(nextElement + "=" + x509.get(nextElement));
					}
					System.out.println("end=============");
				}
				System.out.println(key + "[" + cert.get(key).getClass() + "]=" + cert.get(key));
			}
			
			System.out.println("principal=" + cert.getSubjectX500Principal());
			
			System.out.println("issuer=" + cert.getIssuerDN());
			
//			System.out.println("cert class = " + cert.getClass().getName());
//
//			System.out.println("cert = " + cert);
//			
//			System.out.println("public key = " + pubkey);
//
//			System.out.println("private key = " + prikey);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
