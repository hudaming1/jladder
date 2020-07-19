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
 * JAVA代码解析CSR
 * 替代方案：可以使用「openssl req -text -noout -in csr/rootca.csr.pem」命令查看
 * @author hudaming
 */
public class ReadCsr_forBase64 {

	private static Logger LOG = Logger.getLogger(ReadCsr_forBase64.class.getName());

	private static final String COUNTRY = "2.5.4.6";
	private static final String STATE = "2.5.4.8";
	private static final String LOCALE = "2.5.4.7";
	private static final String ORGANIZATION = "2.5.4.10";
	private static final String ORGANIZATION_UNIT = "2.5.4.11";
	private static final String COMMON_NAME = "2.5.4.3";
	private static final String EMAIL = "2.5.4.9";

	private static final String csrPEM = "-----BEGIN CERTIFICATE REQUEST-----\n" + 
			"MIIE2zCCAsMCAQAwgZUxCzAJBgNVBAYTAkNOMRAwDgYDVQQIDAdTaGFhblhpMQ0w\n" + 
			"CwYDVQQHDARYaWFuMRMwEQYDVQQKDApOaWNrTGkgTHRkMRYwFAYDVQQLDA1OaWNr\n" + 
			"TGkgTHRkIENBMRcwFQYDVQQDDA5OaWNrTGkgUm9vdCBDQTEfMB0GCSqGSIb3DQEJ\n" + 
			"ARYQbGpmcG93ZXJAMTYzLmNvbTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoC\n" + 
			"ggIBALwRnG5E5R0/UcV64dps0HqndPrpBwqZWKHROKP6ivf8bTyrABfX3YIIAFa0\n" + 
			"pj4YYwZRPeZ9YkElALqftMH1tCdVylw7cqChPhf6K1Z3TgNfxcjvuFMUGJ4WvoZT\n" + 
			"TeJkt0afbWdnjVeIyQK1EIS3UawXhN0xprKxxJHyJMe64zU+lKtG9oLl+RcUNxpS\n" + 
			"Uk2eoYg/DKmg67/f+MGrHBLjdjBj+c5inUsp3zPbO29vza42Nn9Q4o8MC+dPpG81\n" + 
			"h6+xWVLXLqESS5wAitnSNu1JfJZTO9W6dgIwCIM9z3zcEFhJ07QQq/aAkb9ZgEsc\n" + 
			"UhHorzMxmFqnQMEDhIT+Pwb1W+w5pjv5jTK7phYDaJKHr7ptFc/j/pe3OYks6ZfJ\n" + 
			"hOjOMqAPoBbtqgqJ4AL3MQqNrosnahOl7cfJ/Ywalbkmcw5xmU9XBVP8k3elBNSc\n" + 
			"84LIQzVRuFt3svkZUc+RIwo4CfrtT6bQJblmx4hzmdXh6zi4akIKnbeNGdNyHI1E\n" + 
			"AxGUrKZ+Fbn+VASX1gfuJ1hsFJFvoHfwqnLTdtQG+DC4WYBxXiAlZMun1gPr9UQv\n" + 
			"E21R/LgsH/rxw7AnPX7u0FNGratQlXA4JDVbVyiTV2lt9e4PP6f9kBB3uPuQomoy\n" + 
			"BW6D7GBHk4vuQUOiVWXsLht1qdw330ea3JRdHGSYxW8ZX4BjAgMBAAGgADANBgkq\n" + 
			"hkiG9w0BAQsFAAOCAgEALQo6vRkMg/laORqhd1LYbfoGVyoWTtSjXF5tma1vA08g\n" + 
			"DDEB3WYGUbJLtLrEY/dRYjfF5+cB0WNEI0ITTeO4JrgwQfuKtDGCEtbnNwoAroPk\n" + 
			"5G/a4kBLw+tlDjhQ4HYhZnB7mwudXjAlD6cKNMYi5PT8uu/XMMYMJz+YlkxsKPho\n" + 
			"JEX3+397EmKKTakuHmHNqDLq/a0g1nqzcdKZaWidF5oZvfdu0la8h72ZaYfpA90w\n" + 
			"ykAv1t+Nqrkg67APFzRVv/vyxGpe+dYiP9UcQSGfzGfjF4EvY3k5jl+YOEfQz/gn\n" + 
			"Sf1yamMPryqurxWJApKTQshajA7E+QRfgaun4MX+EOAoZ2UewEbkrfwGL4FdT+KN\n" + 
			"41FRINRYn/WW7RzNea9iITQF15ns9bN6Qnh0Pn5fKBC2vkUck6+cZ7Bp7Pipq0yH\n" + 
			"4MiLvRwN8YCxvLJGczj4JH2S6GBTg1TLw1N27rnVxsIIFa4Ly4v/hQaiFiaJ2+QP\n" + 
			"SCyhCXL7gQshqMIWpfjE30PVxdq3FI5vhUVKs3u23/N+tpqEpM0xeW+HzCeIh6eq\n" + 
			"2jl7wEHS/72eGSFsJjFWi+a9sLDkLEShHpuXjsxUooJ4ossCW3oT2oZlM3EpdklC\n" + 
			"ddIRSlJsuu3Y4avjQq/oqu/DCfqANtR1vENPgDZYuJY8s8UZ2KwXtfZGCGqM1Tc=\n" + 
			"-----END CERTIFICATE REQUEST-----\n";

	public static void main(String[] args) {
		InputStream stream = new ByteArrayInputStream(csrPEM.getBytes(StandardCharsets.UTF_8));

		ReadCsr_forBase64 m = new ReadCsr_forBase64();
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
