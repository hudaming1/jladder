package org.hum.nettyproxy.test.https_client.ca.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import sun.security.pkcs.PKCS7;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertAttrSet;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class Test2 {
	public static void createKeyStore(File store, String storePass, X500Name ca, String caPass) {

		// 设置证书密钥类型和签名类型
		sun.security.tools.keytool.CertAndKeyGen cak = null;
		try {
			cak = new sun.security.tools.keytool.CertAndKeyGen("RSA", "SHA1withRSA", null);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 设置安全随机数
		SecureRandom secureRandom = null;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cak.setRandom(secureRandom);

		// 产生随机的公私密钥对 1024 bits
		try {
			cak.generate(1024);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 设置证书验证链
		X509Certificate[] certs = { certificate };

		// 将CA的别名，私钥，密码存入keystore中
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance("JKS");
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			keyStore.load(null, storePass.toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			keyStore.setKeyEntry("CA", cak.getPrivateKey(), caPass.toCharArray(), certs);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(store);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			keyStore.store(fos, storePass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	 */
	public static void createSubjectCert(String certAlias, String subjectPasswd, File Store,
			String storePass, String CAname, String CApass) {

		// 加载证书库
		KeyStore keyStore = null;

		try {
			keyStore = KeyStore.getInstance("JKS");
		} catch (KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			keyStore.load(new FileInputStream(Store), storePass.toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 获取ca证书
		X509Certificate caCert = null;
		try {
			caCert = (X509Certificate) keyStore.getCertificate(CAname);
		} catch (KeyStoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// 产生公私密钥对信息
		sun.security.tools.keytool.CertAndKeyGen certAndKeyGen = null;
		try {
			certAndKeyGen = new sun.security.tools.keytool.CertAndKeyGen("RSA", "SHA1withRSA");
		} catch (NoSuchAlgorithmException e) {
			/**********/
			e.printStackTrace();
		}

		SecureRandom secureRandom = null;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		certAndKeyGen.setRandom(secureRandom);

		try {
			certAndKeyGen.generate(1024);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 签名算法
		String sigAlg = "MD5WithRSA";

		// 有效期 30年
		long validity = 30 * 365 * 24L * 60L * 60L;
		Date firstDate = new Date();
		Date lastDate = new Date(firstDate.getTime() + validity);

		CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

		/******* 设置条目信息 *******/
		X509CertInfo x509Info = new X509CertInfo();

		// 版本信息
		try {
			x509Info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
		} catch (CertificateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 序列号
		try {
			x509Info.set(X509CertInfo.SERIAL_NUMBER,
					new CertificateSerialNumber(new java.util.Random().nextInt() & 0x7fffffff));
		} catch (CertificateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		AlgorithmId algID = null;
		try {
			algID = AlgorithmId.get(sigAlg);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 签名算法信息
		try {
			x509Info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algID));
		} catch (CertificateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 条目主体信息
		try {
			x509Info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(info.subject));
		} catch (CertificateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 设置颁发者
		X500Name caInfo = null;
		try {
			caInfo = new X500Name(caCert.getIssuerX500Principal().toString());
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			x509Info.set(X509CertInfo.ISSUER, new CertificateIssuerName(caInfo));
		} catch (CertificateException | IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// 设置公钥
		try {
			x509Info.set(X509CertInfo.KEY, new CertificateX509Key(certAndKeyGen.getPublicKey()));
		} catch (CertificateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 设置有效期

		try {
			x509Info.set(X509CertInfo.VALIDITY, interval);
		} catch (CertificateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 获取CA私钥
		PrivateKey CAPrivateKey = null;

		try {
			CAPrivateKey = (PrivateKey) keyStore.getKey(CAname, CApass.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 对subject签名
		X509CertImpl cert = new X509CertImpl(x509Info);

		try {
			cert.sign(CAPrivateKey, sigAlg);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 设置证书验证链
		Certificate[] certs = { cert, caCert };

		try {
			keyStore.setKeyEntry(certAlias, certAndKeyGen.getPrivateKey(), subjectPasswd.toCharArray(), certs);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			FileOutputStream fos = new FileOutputStream(Store);
			keyStore.store(fos, storePass.toCharArray());
			fos.close();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
