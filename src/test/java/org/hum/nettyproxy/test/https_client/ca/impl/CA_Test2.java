package org.hum.nettyproxy.test.https_client.ca.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CA_Test2 {
	
	static{
		try{
			Security.addProvider(new BouncyCastleProvider());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, UnrecoverableEntryException {
		// 读取CA证书的JKS文件
		KeyStore caKeyStore = KeyStore.getInstance("PKCS12");
		File file = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/server_cert.p12");
//		File file = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/dynamic/atlas-huming.p12");
		caKeyStore.load(new FileInputStream(file), "123456".toCharArray());
		
		// 读取CA的私钥文件
		PrivateKeyEntry caPrivateKey = (PrivateKeyEntry) caKeyStore.getEntry("nickli", new PasswordProtection("123456".toCharArray()));
		
		// 给alice签发证书并存为xxx-alice.jks的文件
		String subject = "C=CN,ST=GuangDong,L=Shenzhen,O=Skybility,OU=Cloudbility,CN=*.163.com,E=huming@163.com,Name=huming";
		gen(caPrivateKey, subject, "*.163.com");
	}

	// 用ke所代表的CA给subject签发证书，并存储到名称为name的jks文件里面。
	public static void gen(PrivateKeyEntry caPrivateKey, String subject, String name) {
		try {
			X509Certificate caCert = (X509Certificate) caPrivateKey.getCertificate();
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair keyPair = kpg.generateKeyPair();

			KeyStore store = KeyStore.getInstance("PKCS12");
			store.load(null, null);
			String issuer = caCert.getIssuerDN().toString();
			Certificate cert = generateV3(issuer, subject, BigInteger.ZERO,
					new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24),
					new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 32), keyPair.getPublic(), // 待签名的公钥
					caPrivateKey.getPrivateKey() // CA的私钥
			);
			
			store.setKeyEntry("aaa", keyPair.getPrivate(), "123456".toCharArray(), new Certificate[] { cert, caCert });
			File file = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/dynamic/atlas-" + name + ".p12");
			if (file.exists() || file.createNewFile()) {
				store.store(new FileOutputStream(file), "123456".toCharArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Certificate generateV3(String issuer, String subject, BigInteger serial, Date notBefore,
			Date notAfter, PublicKey publicKey, PrivateKey privKey)
			throws OperatorCreationException, CertificateException, IOException {

		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(issuer), serial, notBefore,
				notAfter, new X500Name(subject), publicKey);
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privKey);
		// privKey是CA的私钥，publicKey是待签名的公钥，那么生成的证书就是被CA签名的证书。
		X509CertificateHolder holder = builder.build(sigGen);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream is1 = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
		X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
		is1.close();
		return theCert;
	}
}
