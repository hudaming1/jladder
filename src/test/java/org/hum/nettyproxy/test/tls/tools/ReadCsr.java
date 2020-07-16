package org.hum.nettyproxy.test.tls.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.logging.Logger;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

/**
 * 解析CSR
 * @author hudaming
 */
public class ReadCsr {

	private static Logger LOG = Logger.getLogger(ReadCsr.class.getName());

	private static final String COUNTRY = "2.5.4.6";
	private static final String STATE = "2.5.4.8";
	private static final String LOCALE = "2.5.4.7";
	private static final String ORGANIZATION = "2.5.4.10";
	private static final String ORGANIZATION_UNIT = "2.5.4.11";
	private static final String COMMON_NAME = "2.5.4.3";
	private static final String EMAIL = "2.5.4.9";

	private static final String csrPEM = "-----BEGIN CERTIFICATE REQUEST-----\n"
			+ "MIIBVTCBvwIBADAWMRQwEgYDVQQDDAsqLmJhaWR1LmNvbTCBnzANBgkqhkiG9w0B\n"
			+ "AQEFAAOBjQAwgYkCgYEA8rNBGVCn1qClhjFvee0hLhpBRuc96R8IczaXgWHMnhyY\n"
			+ "FQTXwljpv8r/WlFxwMzlMwCgyBq8Zk+2Bm9Z+Tq1tX0XOjUPePcNPcgzBXK105BX\n"
			+ "5u+nl7A2OZfHYEGZkZ+QAY4jSjC5T8k7l/lO81PqXcdE0hrsCdlXxxBBGeyd1VUC\n"
			+ "AwEAAaAAMA0GCSqGSIb3DQEBCwUAA4GBAJHxgI0fp8oxWxIwxYYz35IoO3vKKHtu\n"
			+ "yWsERBT4cG5+qP/i/XJnzCz35llHq0RynDjOAlV0FB9I8AHT/PH+FZ6fS7Yd8xFN\n"
			+ "ojD1BYIXrkbJsgpeIBF2N6HygcBt2br1q5vkTAgiofyF/dgeV5YyLE3VxMTOosfl\n" + "kHfM+ukmC1rZ\n"
			+ "-----END CERTIFICATE REQUEST-----\n";

	public static void main(String[] args) {
		InputStream stream = new ByteArrayInputStream(csrPEM.getBytes(StandardCharsets.UTF_8));

		ReadCsr m = new ReadCsr();
		m.readCertificateSigningRequest(stream);
	}

	public String readCertificateSigningRequest(InputStream csrStream) {

		PKCS10CertificationRequest csr = convertPemToPKCS10CertificationRequest(csrStream);
		String compname = null;

		if (csr == null) {
			LOG.info("FAIL! conversion of Pem To PKCS10 Certification Request");
		} else {
			X500Name x500Name = csr.getSubject();

			System.out.println("x500Name is: " + x500Name + "\n");

			if (x500Name.getRDNs(BCStyle.EmailAddress).length > 0) {
				RDN cn = x500Name.getRDNs(BCStyle.EmailAddress)[0];
				System.out.println(cn.getFirst().getValue().toString());
				System.out.println(x500Name.getRDNs(BCStyle.EmailAddress)[0]);
			}
			System.out.println("COUNTRY: " + getX500Field(COUNTRY, x500Name));
			System.out.println("STATE: " + getX500Field(STATE, x500Name));
			System.out.println("LOCALE: " + getX500Field(LOCALE, x500Name));
			System.out.println("ORGANIZATION: " + getX500Field(ORGANIZATION, x500Name));
			System.out.println("ORGANIZATION_UNIT: " + getX500Field(ORGANIZATION_UNIT, x500Name));
			System.out.println("COMMON_NAME: " + getX500Field(COMMON_NAME, x500Name));
			System.out.println("EMAIL: " + getX500Field(EMAIL, x500Name));
		}
		return compname;
	}

	private String getX500Field(String asn1ObjectIdentifier, X500Name x500Name) {
		RDN[] rdnArray = x500Name.getRDNs(new ASN1ObjectIdentifier(asn1ObjectIdentifier));

		String retVal = null;
		for (RDN item : rdnArray) {
			retVal = item.getFirst().getValue().toString();
		}
		return retVal;
	}

	private PKCS10CertificationRequest convertPemToPKCS10CertificationRequest(InputStream pem) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		PKCS10CertificationRequest csr = null;
		ByteArrayInputStream pemStream = null;

		pemStream = (ByteArrayInputStream) pem;

		Reader pemReader = new BufferedReader(new InputStreamReader(pemStream));
		PEMParser pemParser = null;
		try {
			pemParser = new PEMParser(pemReader);
			Object parsedObj = pemParser.readObject();
			System.out.println("PemParser returned: " + parsedObj);
			if (parsedObj instanceof PKCS10CertificationRequest) {
				csr = (PKCS10CertificationRequest) parsedObj;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (pemParser != null) {
				try {
					pemParser.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return csr;
	}
}
