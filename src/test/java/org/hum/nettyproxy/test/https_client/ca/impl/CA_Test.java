package org.hum.nettyproxy.test.https_client.ca.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

@SuppressWarnings("restriction")
public class CA_Test {
	
	/**
	 * 产生证书库，并创建CA 别名末认为CA
	 * 
	 * @param store     证书库
	 * @param storePass 证书库密码
	 * @param ca        ca信息
	 * @param caPass    ca密码
	 */
	public static void createCA(File store, String storePass, X500Name ca, String caPass) {

		// 设置证书密钥类型和签名类型
		CertAndKeyGen cak = null;
		try {
			cak = new CertAndKeyGen("RSA", "SHA1withRSA", null);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {

			e.printStackTrace();
		}

		// 设置安全随机数
		SecureRandom secureRandom = null;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {

			e.printStackTrace();
		}

		cak.setRandom(secureRandom);

		// 产生随机的公私密钥对 1024 bits
		try {
			cak.generate(1024);
		} catch (InvalidKeyException e) {

			e.printStackTrace();
		}

		// 设置CA的信息
		X500Name suject = ca;

		// 产生一个自签名的证书
		X509Certificate certificate = null;
		try {
			certificate = cak.getSelfCertificate(suject, new Date(), 50 * 365 * 24L * 60L * 60L);
		} catch (InvalidKeyException | CertificateException | SignatureException | NoSuchAlgorithmException
				| NoSuchProviderException e) {

			e.printStackTrace();
		}
		// 设置证书验证链
		X509Certificate[] certs = { certificate };

		// 将CA的别名，私钥，密码存入keystore中
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance("JKS");
		} catch (KeyStoreException e) {

			e.printStackTrace();
		}

		try {
			keyStore.load(null, storePass.toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {

			e.printStackTrace();
		}

		try {
			keyStore.setKeyEntry("CA", cak.getPrivateKey(), caPass.toCharArray(), certs);
		} catch (KeyStoreException e) {

			e.printStackTrace();
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(store);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		try {
			keyStore.store(fos, storePass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {

			e.printStackTrace();
		}

		try {
			fos.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 创建开发者证书库条目
	 * 
	 * @param info          主体条目信息
	 * @param certAlias     条目别名 根据密钥库个数产生
	 * @param subjectPasswd 主体密码 随机产生6位字符串
	 * @param Store         密钥库
	 * @param storePass     密钥库密码
	 * @param CAname        CA名称
	 * @param CApass        CA密码
	 * @throws IOException 
	 * @throws CertificateException 
	 */
	public static void createSubjectCert(String certAlias, String subjectPasswd, File Store, String storePass,
			String CAname, String CApass) throws Exception {

		// 加载证书库
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(new FileInputStream(Store), storePass.toCharArray());

		// 获取ca证书
		X509Certificate caCert = (X509Certificate) keyStore.getCertificate(CAname);

		// 产生公私密钥对信息
		CertAndKeyGen certAndKeyGen = new CertAndKeyGen("RSA", "SHA1withRSA");
		certAndKeyGen.setRandom(SecureRandom.getInstance("SHA1PRNG", "SUN"));
		certAndKeyGen.generate(1024);

		// 有效期 30年
		long validity = 30 * 365 * 24L * 60L * 60L;
		Date firstDate = new Date();
		Date lastDate = new Date(firstDate.getTime() + validity);

		CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

		/******* 设置条目信息 *******/
		X509CertInfo x509Info = new X509CertInfo();

		// 版本信息
		x509Info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));

		// 序列号
		x509Info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new java.util.Random().nextInt() & 0x7fffffff));

		// 签名算法信息
		x509Info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(AlgorithmId.get("MD5WithRSA")));

		// 条目主体信息
		x509Info.set(X509CertInfo.SUBJECT, new X500Name("CN=*.163.com"));

		// 设置颁发者
		String caInfoString = caCert.getIssuerX500Principal().toString();
		x509Info.set(X509CertInfo.ISSUER, new sun.security.x509.X500Name(caInfoString));

		// 设置公钥
		x509Info.set(X509CertInfo.KEY, new CertificateX509Key(certAndKeyGen.getPublicKey()));

		// 设置有效期
		x509Info.set(X509CertInfo.VALIDITY, interval);

		// 获取CA私钥
		PrivateKey CAPrivateKey = (PrivateKey) keyStore.getKey(CAname, CApass.toCharArray());

		// 对subject签名
		X509CertImpl cert = new X509CertImpl(x509Info);
		
		cert.sign(CAPrivateKey, "MD5WithRSA");

		// 设置证书验证链
		Certificate[] certs = { cert, caCert };

		keyStore.setKeyEntry(certAlias, certAndKeyGen.getPrivateKey(), subjectPasswd.toCharArray(), certs);

		FileOutputStream fos = new FileOutputStream("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/dynamic/huming_test2.p12");
		keyStore.store(fos, storePass.toCharArray());
		fos.close();
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		File store = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/server_cert.p12");
		createSubjectCert("createByJava" + System.currentTimeMillis(), "123456", store, "123456", "nickli", "123456");
	}
}
