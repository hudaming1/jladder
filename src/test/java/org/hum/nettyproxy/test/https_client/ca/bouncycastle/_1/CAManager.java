package org.hum.nettyproxy.test.https_client.ca.bouncycastle._1;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.slf4j.LoggerFactory;

/**
 * Generate and sign X509 SSL certificates
 */

/**
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class CAManager {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CAManager.class);

	static {
		// Load BouncyCastle security provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public void init(Store<KeyPair> keystore, Store<X509Certificate> certstore, X509Attrs principals)
			throws KeyPairException, CertificateException {
		KeyPair keypair = generateKeyPair(keystore);
		generateCACertificate(keypair, certstore, principals);
	}

	/**
	 * 通过上级证书签发
	 * 
	 * @param selfkeypair
	 * @param parentkeypair
	 * @param parentcert
	 * @param attrs
	 * @return
	 * @throws CertificateException
	 */
	public X509Certificate generateIntermediaCert(KeyPair selfkeypair, KeyPair parentkeypair,
			X509Certificate parentcert, X509Attrs attrs) throws CertificateException {
		try {
			X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
			generator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
			generator.setIssuerDN(PrincipalUtil.getSubjectX509Principal(parentcert));
			generator.setNotBefore(new Date(System.currentTimeMillis() - 10000));
			generator.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 50))); // CA valid
																												// for
																												// 50yrs
			generator.setSubjectDN(new X509Principal(attrs.getOrdering(), attrs.getAttrs()));
			generator.setPublicKey(selfkeypair.getPublic());
			generator.setSignatureAlgorithm("SHA256WithRSAEncryption");

			//
			// extensions
			//
			generator.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
					new AuthorityKeyIdentifierStructure(parentcert));
			generator.addExtension(X509Extensions.SubjectKeyIdentifier, false,
					new SubjectKeyIdentifierStructure(selfkeypair.getPublic()));

			generator.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(0));
			generator.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
					KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.cRLSign | KeyUsage.keyCertSign));
			X509Certificate cert = generator.generate(parentkeypair.getPrivate());
			cert.checkValidity(new Date());
			cert.verify(parentcert.getPublicKey());
			// PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) cert;
			//
			// //
			// // this is actually optional - but if you want to have control
			// // over setting the friendly name this is the way to do it...
			// //
			// bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
			// new DERBMPString(attrs.getCommonName()));
			// bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
			// new SubjectKeyIdentifierStructure(selfkeypair.getPublic()));

			return cert;
		} catch (Exception e) {
			throw new CertificateException(e);
		}
	}

	public KeyStore generatePKCS12(X509Certificate[] chain, KeyPair keypair) throws CertificateException {
		try {
			PrivateKey privatekey = keypair.getPrivate();
			PublicKey publickey = keypair.getPublic();
			X509Certificate cert = chain[0];
			//
			// add the friendly name for the private key
			//
			PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) privatekey;

			//
			// this is also optional - in the sense that if you leave this
			// out the keystore will add it automatically, note though that
			// for the browser to recognise which certificate the private key
			// is associated with you should at least use the pkcs_9_localKeyId
			// OID and set it to the same as you do for the private key's
			// corresponding certificate.
			//
			X509Name subjectDN = (X509Name) cert.getSubjectDN();
			String cn = (String) subjectDN.getValues(X509Principal.CN).get(0);
			bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString(cn));
			bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
					new SubjectKeyIdentifierStructure(publickey));

			//
			// store the key and the certificate chain
			//
			KeyStore store = KeyStore.getInstance("PKCS12", "BC");
			store.load(null, null);

			//
			// if you haven't set the friendly name and local key id above
			// the name below will be the name of the key
			//
			store.setKeyEntry(cn, privatekey, null, chain);

			return store;
		} catch (Exception e) {
			throw new CertificateException(e);
		}
	}

	private KeyPair generateKeyPair(Store<KeyPair> store) throws KeyPairException {
		KeyPair keypair = null;
		if (store != null)
			try {
				keypair = store.read();
				logger.debug("Loading CA KeyPair from Store {}", store);
			} catch (StorageException e1) {
			}
		if (keypair != null) {
			logger.debug("Loaded CA KeyPair from Store {}", store);
		} else {
			try {
				keypair = KeyPairManager.generateRSAKeyPair();
				logger.debug("Generating CA KeyPair");
				store.save(keypair, null);
				logger.debug("Saving CA KeyPair to Store {}" + store);
			} catch (Exception e) {
				throw new KeyPairException(e);
			}
		}
		return keypair;
	}

	/**
	 * 若CA文件存在，则直接读取
	 * 
	 * @param keyPair
	 * @param store
	 * @param principals
	 * @return
	 * @throws CertificateException
	 */
	private X509Certificate generateCACertificate(KeyPair keyPair, Store<X509Certificate> store, X509Attrs principals)
			throws CertificateException {
		X509Certificate certificate = null;
		if (store != null)
			try {
				certificate = store.read();
				logger.debug("Loading CA Certificate from Store {}", store);
			} catch (StorageException e1) {
			}
		if (certificate != null) {
			logger.debug("Loaded CA Certificate from Store {}", store);
		} else
			try {
				logger.debug("Generating CA Certificate");
				certificate = generateCACertificate(keyPair, principals);
				store.save(certificate, null);
				logger.debug("Saving CA Certificate to Store {}" + store);
			} catch (Exception e) {
				throw new CertificateException(e);
			}
		return certificate;
	}

	/**
	 * Generates a v1 certificate - suitable for a CA with no usage restrictions
	 * 
	 * @param pair A public/private KeyPair to use for signing the CA certificate
	 * @return A valid v1 X.509 certificate
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws java.security.cert.CertificateException
	 */
	private X509Certificate generateCACertificate(KeyPair pair, X509Attrs principals)
			throws InvalidKeyException, NoSuchProviderException, SignatureException, NoSuchAlgorithmException,
			java.security.cert.CertificateException {

		X509V1CertificateGenerator generator = new X509V1CertificateGenerator();

		generator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		generator.setIssuerDN(new X509Principal(principals.getOrdering(), principals.getAttrs()));
		generator.setNotBefore(new Date(System.currentTimeMillis() - 10000));
		generator.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 50))); // CA valid for
																											// 50yrs
		generator.setSubjectDN(new X509Principal(principals.getOrdering(), principals.getAttrs()));
		generator.setPublicKey(pair.getPublic());
		generator.setSignatureAlgorithm("SHA256WithRSAEncryption");

		X509Certificate cert = generator.generate(pair.getPrivate(), "BC");
		cert.checkValidity(new Date());

		cert.verify(pair.getPublic());

		// PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) cert;

		//
		// this is actually optional - but if you want to have control
		// over setting the friendly name this is the way to do it...
		//
		// bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
		// new DERBMPString(principals.getCommonName()));
		return cert;

	}

	public X509Certificate issueCertificate(PKCS10CertificationRequest request, int days, X509Certificate parentcert,
			KeyPair parentkey, boolean isCA) throws CertificateException {
		try {
			X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
			generator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
			generator.setIssuerDN(parentcert.getSubjectX500Principal());
			generator.setNotBefore(new Date(System.currentTimeMillis()));
			generator.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * days)));
//			generator.setSubjectDN(request.getCertificationRequestInfo().getSubject());
			generator.setPublicKey(request.getPublicKey("BC"));
			generator.setSignatureAlgorithm("SHA256WithRSAEncryption");
			generator.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
					new AuthorityKeyIdentifierStructure(parentcert));
			generator.addExtension(X509Extensions.SubjectKeyIdentifier, false,
					new SubjectKeyIdentifierStructure(request.getPublicKey("BC")));

			/**
			 * 基本用途限制
			 * 
			 * BasicConstraints := SEQUENCE { cA BOOLEAN DEFAULT FALSE, 是否是CA证书
			 * pathLenConstraint INTEGER (0..MAX) OPTIONAL 证书链长度约束 }
			 */
			generator.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(isCA));
			generator.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
					KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.cRLSign | KeyUsage.keyCertSign));
			// generator.addExtension(X509Extensions.ExtendedKeyUsage,
			// true,
			// new ExtendedKeyUsage(purposeId));

			X509Certificate cert = generator.generate(parentkey.getPrivate());
			cert.getSubjectDN();
			cert.checkValidity(new Date());
			cert.verify(parentcert.getPublicKey());
			// PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) cert;
			//
			// //
			// // this is also optional - in the sense that if you leave this
			// // out the keystore will add it automatically, note though that
			// // for the browser to recognise the associated private key this
			// // you should at least use the pkcs_9_localKeyId OID and set it
			// // to the same as you do for the private key's localKeyId.
			// //
			// String cn = (String) request.getCertificationRequestInfo()
			// .getSubject()
			// .getValues(X509Principal.CN)
			// .get(0);
			// bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
			// new DERBMPString(cn));
			// bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
			// new SubjectKeyIdentifierStructure(
			// request.getPublicKey("BC")));

			return cert;
		} catch (Exception e) {
			throw new CertificateException(e);
		}
	}

}
