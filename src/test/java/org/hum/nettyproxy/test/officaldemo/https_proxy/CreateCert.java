package org.hum.nettyproxy.test.officaldemo.https_proxy;

public class CreateCert {

	/**
	 * 利用keytools生成.keystore文件
	 * "C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -genkey -alias tbb -keyalg RSA -keystore d:\tbb.keystore
	 * 
	 * 
	 * "C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -selfcert -alias tbb -keystore d:\tbb.keystore
	 * 
	 * .keystore文件导出.cer文件
	 * "C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -export -alias tbb -keystore d:\tbb.keystore -storepass 123456 -rfc -file d:\tbb.cer
	 * 
	 * .keystore文件导出.p12文件
	 * "C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -importkeystore -srcstoretype JKS -srckeystore d:\tbb.keystore -srcstorepass 123456 -srcalias tbb -srckeypass 123456 -deststoretype PKCS12 -destkeystore d:\tbb.p12 -deststorepass 123456 -destalias client -destkeypass 123456 -noprompt
	 */
	
	/**
	 * open-ssl:
	 * 创建普通的Cert：https://blog.csdn.net/likeyoutoo/article/details/49358809
	 * https://blog.csdn.net/u010983881/article/details/83619603
	 * 带SAN的Cert：https://codeday.me/bug/20170831/60851.html
	 * 
	 */
	
	/**
	 * 参照Fiddler工作原理：
	 * https://www.cnblogs.com/zeyuxi/articles/8927061.html
	 * https://www.cnblogs.com/pathbreaker/articles/10401333.html
	 * https://www.cnblogs.com/sucretan2010/p/11526467.html
	 */
}

