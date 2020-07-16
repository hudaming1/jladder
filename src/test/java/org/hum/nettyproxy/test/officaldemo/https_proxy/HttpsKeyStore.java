package org.hum.nettyproxy.test.officaldemo.https_proxy;

import java.io.FileInputStream;
import java.io.InputStream;

public class HttpsKeyStore {

	public static InputStream getKeyStoreStream() {
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(Arguments.keystorePath);
		} catch (Exception e) {
			System.out.println("读取密钥文件失败, path=" + Arguments.keystorePath);
			e.printStackTrace();
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
	// 1.客户端浏览器需要信任：/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/certs/rootca.cert.pem
	// 2.
	public static String keystorePath = "/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/server/baidu_server.p12";
//	public static String keystorePath = "/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/dynamic/huming_test2.p12";
//	public static String keystorePath = "/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/_20200625/cert4openssl.p12";
	public static String certificatePassword = "123456";
	public static String keystorePassword = "123456";
}
