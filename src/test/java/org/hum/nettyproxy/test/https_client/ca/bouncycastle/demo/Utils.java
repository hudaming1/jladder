package org.hum.nettyproxy.test.https_client.ca.bouncycastle.demo;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Chapter 8 Utils
 */
public class Utils {

	private static final int VALIDITY_PERIOD = 7 * 24 * 60 * 60 * 1000; // one week

	/**
	 * Generate a sample V1 certificate to use as a CA root certificate
	 */
	public static X509Certificate generateRootCert(KeyPair pair) throws Exception {
		X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();

		certGen.setSerialNumber(BigInteger.valueOf(1));
		certGen.setIssuerDN(new X500Principal("CN=Test CA Certificate"));
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
		certGen.setSubjectDN(new X500Principal("CN=Test CA Certificate"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

		return certGen.generateX509Certificate(pair.getPrivate(), "BC");
	}

	/**
	 * Generate a sample V3 certificate to use as an intermediate CA certificate
	 */
	public static X509Certificate generateIntermediateCert(PublicKey intKey, PrivateKey caKey, X509Certificate caCert)
			throws Exception {
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		certGen.setSerialNumber(BigInteger.valueOf(1));
		certGen.setIssuerDN(caCert.getSubjectX500Principal());
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
		certGen.setSubjectDN(new X500Principal("CN=Test Intermediate Certificate"));
		certGen.setPublicKey(intKey);
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

		certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(intKey));
		certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(0));
		certGen.addExtension(X509Extensions.KeyUsage, true,
				new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));

		return certGen.generateX509Certificate(caKey, "BC");
	}

	/**
	 * Generate a sample V3 certificate to use as an end entity certificate
	 */
	public static X509Certificate generateEndEntityCert(PublicKey entityKey, PrivateKey caKey, X509Certificate caCert)
			throws Exception {
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		certGen.setSerialNumber(BigInteger.valueOf(1));
		certGen.setIssuerDN(caCert.getSubjectX500Principal());
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
		certGen.setSubjectDN(new X500Principal("CN=Test End Certificate"));
		certGen.setPublicKey(entityKey);
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

		certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(entityKey));
		certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
		certGen.addExtension(X509Extensions.KeyUsage, true,
				new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

		return certGen.generateX509Certificate(caKey, "BC");
	}

	public static String ROOT_ALIAS = "root";
	public static String INTERMEDIATE_ALIAS = "intermediate";
	public static String END_ENTITY_ALIAS = "end";

	/**
	 * Generate a X500PrivateCredential for the root entity.
	 */
	public static X500PrivateCredential createRootCredential() throws Exception {
		KeyPair rootPair = generateRSAKeyPair();
		X509Certificate rootCert = generateRootCert(rootPair);

		return new X500PrivateCredential(rootCert, rootPair.getPrivate(), ROOT_ALIAS);
	}

	/**
	 * Generate a X500PrivateCredential for the intermediate entity.
	 */
	public static X500PrivateCredential createIntermediateCredential(PrivateKey caKey, X509Certificate caCert)
			throws Exception {
		KeyPair interPair = generateRSAKeyPair();
		X509Certificate interCert = generateIntermediateCert(interPair.getPublic(), caKey, caCert);

		return new X500PrivateCredential(interCert, interPair.getPrivate(), INTERMEDIATE_ALIAS);
	}

	/**
	 * Generate a X500PrivateCredential for the end entity.
	 */
	public static X500PrivateCredential createEndEntityCredential(PrivateKey caKey, X509Certificate caCert)
			throws Exception {
		KeyPair endPair = generateRSAKeyPair();
		X509Certificate endCert = generateEndEntityCert(endPair.getPublic(), caKey, caCert);

		return new X500PrivateCredential(endCert, endPair.getPrivate(), END_ENTITY_ALIAS);
	}

	/**
	 * Create a random 1024 bit RSA key pair
	 */
	public static KeyPair generateRSAKeyPair() throws Exception {
		org.bouncycastle.jce.provider.JDKKeyPairGenerator kpGen = (org.bouncycastle.jce.provider.JDKKeyPairGenerator) org.bouncycastle.jce.provider.JDKKeyPairGenerator.getInstance("RSA", "BC");

		kpGen.initialize(1024, new SecureRandom());

		return kpGen.generateKeyPair();
	}

	public static SecretKey createKeyForAES(int bitLength, SecureRandom random) throws Exception {
		KeyGenerator generator = KeyGenerator.getInstance("AES", "BC");

		generator.init(256, random);

		return generator.generateKey();
	}
}