package org.hum.nettyproxy.test.https_client.ca.bouncycastle._2;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Base64Encoder;

public class CreateCA {

	public static void main(String[] args) {
		KeyPairGenerator rsa = (KeyPairGenerator) KeyPairGenerator.getInstance("RSA");
		rsa.initialize(4096);
		KeyPair kp = rsa.generateKeyPair();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);

		byte[] pk = kp.getPublic().getEncoded();
		SubjectPublicKeyInfo bcPk = SubjectPublicKeyInfo.getInstance(pk);

		X509v1CertificateBuilder certGen = new X509v1CertificateBuilder(new X500Name("CN=CA Cert"), BigInteger.ONE,
				new Date(), cal.getTime(), new X500Name("CN=CA Cert"), bcPk);

		X509CertificateHolder certHolder = certGen
				.build(new JcaContentSignerBuilder("SHA1withRSA").build(kp.getPrivate()));

		Base64Encoder encoder = new Base64Encoder();

		System.out.println("CA CERT");
		encoder.encode(certHolder.getEncoded(), 0, certHolder.getEncoded().length, out);

		System.exit(0);
	}
}
