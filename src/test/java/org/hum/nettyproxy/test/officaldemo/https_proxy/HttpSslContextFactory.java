package org.hum.nettyproxy.test.officaldemo.https_proxy;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class HttpSslContextFactory {

	private static final String PROTOCOL = "SSLv3";// 客户端可以指明为SSLv3或者TLSv1.2
	/** 针对于服务器端配置 */
	private static SSLContext sslContext = null;

	public static SSLEngine createSSLEngine(String domain) {
		SSLContext serverContext = null;
		try {
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(HttpsKeyStore.getKeyStoreStream(domain), HttpsKeyStore.getKeyStorePassword());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, HttpsKeyStore.getCertificatePassword());
			serverContext = SSLContext.getInstance(PROTOCOL);
			serverContext.init(kmf.getKeyManagers(), null, null);
		} catch (Exception e) {
			System.out.println("初始化server SSL失败 " + e);
			throw new Error("Failed to initialize the server SSLContext", e);
		}
		sslContext = serverContext;
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(false);
        return sslEngine ;
    }
}
