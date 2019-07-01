package org.hum.nettyproxy.test.officaldemo.https_server;

public class CreateCert {

	/**
	 * "C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -genkey -alias tbb -keyalg RSA -keystore d:\tbb.keystore
	 * 
	 * "C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -selfcert -alias tbb -keystore d:\tbb.keystore
	 * 
	 * "C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -export -alias tbb -keystore d:\tbb.keystore -storepass 123456 -rfc -file d:\tbb.cer
	 */
	
	/**
	 * open-ssl:
	 * 创建普通的Cert：https://blog.csdn.net/likeyoutoo/article/details/49358809
	 * https://blog.csdn.net/u010983881/article/details/83619603
	 * 带SAN的Cert：https://codeday.me/bug/20170831/60851.html
	 * 
	 */
}

