package org.hum.nettyproxy.test.officaldemo.https_proxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class HttpsKeyStore {

	public static InputStream getKeyStoreStream() {
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(Arguments.keystorePath);
		} catch (FileNotFoundException e) {
			System.out.println("读取密钥文件失败 " + e);
		}
		return inStream;
	}

	public static char[] getCertificatePassword() {
		return Arguments.certificatePassword.toCharArray();
	}

	public static char[] getKeyStorePassword() {
		return Arguments.keystorePassword.toCharArray();
	}
}

class Arguments {
	public static String keystorePath = "/Users/hudaming/Workspace/GitHub/springserver/springserver/cert/keystore.p12";
//	public static String keystorePath = "/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/https_client/ca/server.p12";
	public static String certificatePassword = "123456";
	public static String keystorePassword = "123456";
}
