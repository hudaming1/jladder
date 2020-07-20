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
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * @author hudaming
 * 参考 https://blog.csdn.net/cwjcsu/article/details/9217139
 */
public class CreateCert_forJava {

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
		KeyStore caStore = KeyStore.getInstance("PKCS12");
		File caFile = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/ca_and_cert/myca/rootca/server_cert.p12");
		caStore.load(new FileInputStream(caFile), "123456".toCharArray());

		// 给alice签发证书并存为server_cert.p12的文件
		PrivateKeyEntry privateKey = (PrivateKeyEntry) caStore.getEntry("nickli", new PasswordProtection("123456".toCharArray()));
		String subject = "CN=*.baidu.com";
		gen(privateKey, subject, "huming");
	}

	// 用KeyEntry形式存储一个私钥以及对应的证书，并把CA证书加入到它的信任证书列表里面。
	public static void store(PrivateKey key, Certificate cert, Certificate caCert, String name)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore store = KeyStore.getInstance("PKCS12");
		store.load(null, null);
		// 这两个为什么顺序不同？ 
		System.out.println(((sun.security.x509.X509CertImpl)cert).getIssuerDN());
		System.out.println(((sun.security.x509.X509CertImpl)caCert).getSubjectDN());
//		System.out.println(validateChain(new Certificate[] { cert, caCert }));
		store.setKeyEntry("nickli", key, "123456".toCharArray(), new Certificate[] { cert, caCert });
		File file = new File("/Users/hudaming/Workspace/GitHub/netty-proxy/src/test/java/org/hum/nettyproxy/test/officaldemo/_20200720/cert4java.p12");
		if (file.exists() || file.createNewFile()) {
			store.store(new FileOutputStream(file), "123456".toCharArray());
		}
	}
//	C=CN, ST=ShaanXi, O=NickLi Ltd, OU=NickLi Ltd CA, CN=NickLi Root CA, EMAILADDRESS=ljfpower@163.com
//	EMAILADDRESS=ljfpower@163.com, CN=NickLi Root CA, OU=NickLi Ltd CA, O=NickLi Ltd, ST=ShaanXi, C=CN		
    private static boolean validateChain(Certificate[] certChain) throws IOException
    {
        for (int i = 0; i < certChain.length-1; i++) {
        	// getRFC2253CanonicalName
            X500Principal issuerDN = ((X509Certificate)certChain[i]).getIssuerX500Principal();
            X500Principal subjectDN = ((X509Certificate)certChain[i+1]).getSubjectX500Principal();
            System.out.println(issuerDN.getName());
            System.out.println(subjectDN.getName());
            // 这个两个输出顺序不同，导致最终证书链无效
//            System.out.println(new sun.security.x509.X500Name(issuerDN.getName()).getRFC2253CanonicalName());;
//            System.out.println(new sun.security.x509.X500Name(subjectDN.getName()).getRFC2253CanonicalName());;
            if (!(issuerDN.equals(subjectDN)))
                return false;
        }
        // Check for loops in the chain. If there are repeated certs,
        // the Set of certs in the chain will contain fewer certs than
        // the chain
        Set<Certificate> set = new HashSet<>(Arrays.asList(certChain));
        return set.size() == certChain.length;
    }

	// 用ke所代表的CA给subject签发证书，并存储到名称为name的jks文件里面。
	public static void gen(PrivateKeyEntry ke, String subject, String name) {
		try {
			X509Certificate caCert = (X509Certificate) ke.getCertificate();
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair keyPair = kpg.generateKeyPair();

			KeyStore store = KeyStore.getInstance("PKCS12");
			store.load(null, null);
			String issuer = caCert.getIssuerDN().toString();
			Certificate cert = generateV3(issuer, subject, BigInteger.ZERO,
					new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24),
					new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 32), keyPair.getPublic(), // 待签名的公钥
					ke.getPrivateKey()// CA的私钥
					, null);
			store(keyPair.getPrivate(), cert, ke.getCertificate(), name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Certificate generateV3(String issuer, String subject, BigInteger serial, Date notBefore,
			Date notAfter, PublicKey publicKey, PrivateKey privKey, List<Extension> extensions)
			throws OperatorCreationException, CertificateException, IOException {
		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(issuer), serial, notBefore,
				notAfter, new X500Name(subject), publicKey);
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privKey);
		// privKey是CA的私钥，publicKey是待签名的公钥，那么生成的证书就是被CA签名的证书。
		if (extensions != null)
			for (Extension ext : extensions) {
				builder.addExtension(new ASN1ObjectIdentifier(ext.getId()), ext.isCritical(),
						ASN1Primitive.fromByteArray(ext.getValue()));
			}
		X509CertificateHolder holder = builder.build(sigGen);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream is1 = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
		X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
		is1.close();
		return theCert;
	}
}
