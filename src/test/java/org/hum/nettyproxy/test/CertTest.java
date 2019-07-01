package org.hum.nettyproxy.test;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

public class CertTest {

	public static void main(String[] args) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			FileInputStream instream = new FileInputStream(new File("d:/serverKeystore.jks"));
			try {
				trustStore.load(instream, "123456".toCharArray());
			} finally {
				instream.close();
			}
		} catch (Exception ce) {
			ce.printStackTrace();
		}
	}
}
