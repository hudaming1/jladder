package org.hum.tls.https_client.ca.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.DEROctetString;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import sun.security.util.ObjectIdentifier;

@SuppressWarnings("restriction")
public class CreateCertOrCA_forJava {

	/**
	 * 产生证书库，并创建CA 别名末认为CA
	 */
	public static void createCA(File outPath, String storePass, X500Name caSubject, String caPass) throws Exception {

		// 设置证书密钥类型和签名类型
		CertAndKeyGen cak = new CertAndKeyGen("RSA", "SHA1withRSA", null);

		// 设置安全随机数
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		cak.setRandom(secureRandom);
		cak.generate(1024);
		
		sun.security.x509.CertificateExtensions extensions = new sun.security.x509.CertificateExtensions();
		// TODO
		String oid = "";
		DEROctetString octetString = new DEROctetString("".getBytes());
		extensions.set(oid, new sun.security.x509.Extension(new ObjectIdentifier(oid), false, octetString.toASN1Primitive().getEncoded()));
		
		// 产生一个自签名的证书
		X509Certificate certificate = cak.getSelfCertificate(caSubject, new Date(), 50 * 365 * 24L * 60L * 60L);
		
		// 设置证书验证链
		X509Certificate[] certs = { certificate };

		// 将CA的别名，私钥，密码存入keystore中
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, storePass == null ? null : storePass.toCharArray());
		keyStore.setKeyEntry("CA", cak.getPrivateKey(), caPass.toCharArray(), certs);

		FileOutputStream fos = new FileOutputStream(outPath);
		keyStore.store(fos, storePass == null ? null : storePass.toCharArray());
		fos.close();
	}

	/**
	 * 创建开发者证书库条目
	 * 
	 * @param info          主体条目信息
	 * @param certAlias     条目别名 根据密钥库个数产生
	 * @param subjectPasswd 主体密码 随机产生6位字符串
	 * @param caStore       密钥库
	 * @param caStorePass   密钥库密码
	 * @param CAname        CA名称
	 * @param CApass        CA密码
	 * @throws IOException
	 * @throws CertificateException
	 */
	public static void createSubjectCert(String certAlias, String subjectPasswd, File caStore, String caStorePass,
			String CAname, String CApass) throws Exception {

		// 加载证书库
		KeyStore caKeyStore = KeyStore.getInstance("PKCS12");
		caKeyStore.load(new FileInputStream(caStore), caStorePass.toCharArray());

		// 获取CA证书
		X509Certificate caCert = (X509Certificate) caKeyStore.getCertificate(CAname);

		// 有效期30年
		long validity = 1 * 365 * 24L * 60L * 60L;
		Date firstDate = new Date();
		Date lastDate = new Date(firstDate.getTime() + validity);
		CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

		// 产生公私密钥对信息
		CertAndKeyGen certAndKeyGen = new CertAndKeyGen("RSA", "SHA1withRSA");
		certAndKeyGen.setRandom(SecureRandom.getInstance("SHA1PRNG", "SUN"));
		certAndKeyGen.generate(1024);

		/******* 设置条目信息 *******/
		X509CertInfo x509Info = new X509CertInfo();

		// 版本信息
		x509Info.set(X509CertInfo.VERSION, new sun.security.x509.CertificateVersion(sun.security.x509.CertificateVersion.V1));

		// 序列号
		x509Info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new java.util.Random().nextInt() & 0x7fffffff));

		// 签名算法信息
		x509Info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(AlgorithmId.get("SHA1withRSA")));

		// 条目主体信息
		x509Info.set(X509CertInfo.SUBJECT, new X500Name("CN=*.baidu.com"));

		// 设置颁发者
		x509Info.set(X509CertInfo.ISSUER, new X500Name(caCert.getSubjectX500Principal().toString()));

		// 设置公钥
		x509Info.set(X509CertInfo.KEY, new CertificateX509Key(certAndKeyGen.getPublicKey()));

		// 设置有效期
		x509Info.set(X509CertInfo.VALIDITY, interval);

		// 获取CA私钥
		PrivateKey CAPrivateKey = (PrivateKey) caKeyStore.getKey(CAname, CApass.toCharArray());

		// 对subject签名
		X509CertImpl cert = new X509CertImpl(x509Info);
		cert.sign(CAPrivateKey, "SHA1withRSA");

		// 设置证书验证链
		Certificate[] certs = { cert, caCert };

		KeyStore certKeyStore = KeyStore.getInstance("PKCS12");
		certKeyStore.load(null, null);

		certKeyStore.setKeyEntry(certAlias, certAndKeyGen.getPrivateKey(), subjectPasswd.toCharArray(), certs);

		String outpath = "/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/_20200625/cert4java.p12";

		System.out.println(new File(outpath).delete());

		FileOutputStream fos = new FileOutputStream(outpath);
		certKeyStore.store(fos, caStorePass.toCharArray());
		fos.close();
	}
	
	public static void main2(String[] args) throws IOException, Exception {
		File outCaFile = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/_20200625/ca4java.p12");
		createCA(outCaFile, "123456", new X500Name("EMAILADDRESS=ljfpower@163.com, CN=NickLi Root CA, OU=NickLi Ltd, O=NickLi, ST=ShaanXi, C=CN"), "123456");
	}

	/**
	 * 目前通过Java生成的已经和用openssl生成的cert基本保持一致了（详细对比，只有有效期和序列号不同，其他关键信息例如签名算法，CA信息完全一致）
	 * 但调用时仍然提示「javax.crypto.AEADBadTagException: Tag mismatch」错误
	 */
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		File caFile = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/server_cert.p12");
		createSubjectCert("nickli", "123456", caFile, "123456", "nickli", "123456");
		System.out.println(System.currentTimeMillis() - start);
	}
}
