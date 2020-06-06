package org.hum.nettyproxy.test.https_client.ca.impl;

import java.io.ByteArrayInputStream;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

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

	/**
	 * 2020-05-31 搞不定了，留着TODO吧，目前的问题在我看来不是问题，「校验证书链时居然因为字段顺序不同，而导致证书链invaild」
	 * <pre>
	 *    1.解决方案一：目前使用的CA是通过「openssl」颁发的，考虑CA也用Java颁发，然后再颁发证书，是不是就ok了？
	 *    2.解决方案二：能否更改证书内的字段顺序，根据什么取的，还没搞懂
	 * </pre>
	 * @param args
	 */ 
	/**
	 * TODO 2020-0606  0531证书链始终无法通过，目前只是发现Principal的字段顺序不同，导致证书链无法通过，后来在创建时，将issuer
	 * 排好序，然后写死在代码中，发现可以通过证书链校验了。但新的问题是证书无法使用，甚至在Mac中导入钥匙串时报错（密码输入部分能过）
	 * 目前使用「bouncycastle」随意生成一个简单证书，都导入报错，需要查看原因
	 */
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, UnrecoverableEntryException {
		// 读取CA证书的JKS文件
		KeyStore caKeyStore = KeyStore.getInstance("PKCS12");
		File ca_p12_file = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/server_cert.p12");
		caKeyStore.load(new FileInputStream(ca_p12_file), "123456".toCharArray());
		
		// 读取CA的私钥文件
		PrivateKeyEntry caPrivateKey = (PrivateKeyEntry) caKeyStore.getEntry("nickli", new PasswordProtection("123456".toCharArray()));
		
		// 给alice签发证书并存为xxx-alice.jks的文件
		String subject = "C=CN,ST=GuangDong,L=Shenzhen,O=Skybility,OU=Cloudbility,CN=*.163.com,E=huming@163.com,Name=huming";
		signCertByCA(caPrivateKey, subject, "huming");
	}

	public static void signCertByCA(PrivateKeyEntry caPrivateKey, String certSubject, String fileName) {
		try {
			// 创建Certificate的私钥
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair keyPair = kpg.generateKeyPair();

			// 使用CA创建Certificate
			X509Certificate caCert = (X509Certificate) caPrivateKey.getCertificate();
//			String issuer = caCert.getIssuerDN().toString();
			System.out.println(caCert.getIssuerDN().toString());
			String  issuer = "C=CN, ST=ShaanXi, O=NickLi Ltd, OU=NickLi Ltd CA, CN=NickLi Root CA, EMAILADDRESS=ljfpower@163.com";
			System.out.println(issuer);
			
			// 使用「bouncycastle工具类」创建证书
			Certificate cert = generateV3(issuer, certSubject, BigInteger.ZERO, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24),
				new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 32), keyPair.getPublic(), caPrivateKey.getPrivateKey() 
			);
			
			KeyStore store = KeyStore.getInstance("PKCS12");
			store.load(null, null);
			validateChain(new Certificate[] { cert, caCert });
			store.setKeyEntry("huming", keyPair.getPrivate(), "1234356".toCharArray(), new Certificate[] { caCert });
//			store.setKeyEntry("aaa", keyPair.getPrivate(), "1234356".toCharArray(), new Certificate[] { cert, caCert });
			File file = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/dynamic/atlas-" + fileName + ".p12");
			if (file.exists() || file.createNewFile()) {
				file.delete();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			store.store(fileOutputStream, "123456".toCharArray());
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean validateChain(Certificate[] certChain) {
		for (int i = 0; i < certChain.length - 1; i++) {
			// C=CN, ST=ShaanXi, O=NickLi Ltd, OU=NickLi Ltd CA, CN=NickLi Root CA, EMAILADDRESS=ljfpower@163.com
			X500Principal issuerDN = ((X509Certificate) certChain[i]).getIssuerX500Principal();
			// EMAILADDRESS=ljfpower@163.com, CN=NickLi Root CA, OU=NickLi Ltd CA, O=NickLi Ltd, ST=ShaanXi, C=CN
			X500Principal subjectDN = ((X509Certificate) certChain[i + 1]).getSubjectX500Principal();
			if (!(issuerDN.equals(subjectDN)))
				return false;
		}
		Set<Certificate> set = new HashSet<>(Arrays.asList(certChain));
		return set.size() == certChain.length;
	}
	
	// 1.2.840.113549.1.9.1=#16106c6a66706f776572403136332e636f6d,cn=nickli root ca,ou=nickli ltd ca,o=nickli ltd,st=shaanxi,c=cn
	// c=cn,st=shaanxi,o=nickli ltd,ou=nickli ltd ca,cn=nickli root ca,1.2.840.113549.1.9.1=#16106c6a66706f776572403136332e636f6d
	
	public static Certificate generateV3(String issuer, String subject, BigInteger serial, Date notBefore,
			Date notAfter, PublicKey publicKey, PrivateKey privKey)
			throws OperatorCreationException, CertificateException, IOException {

		// 根据参数创建「证书构建器」
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
