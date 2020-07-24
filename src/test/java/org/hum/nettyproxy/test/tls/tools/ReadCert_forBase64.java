package org.hum.nettyproxy.test.tls.tools;

import java.security.cert.X509Certificate;

import sun.security.util.ObjectIdentifier;
import sun.security.x509.Extension;
import sun.security.x509.GeneralNames;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X509CertImpl;

public class ReadCert_forBase64 {
	static public java.security.cert.X509Certificate fromString(String cert) throws Exception {
		java.security.cert.CertificateFactory certificateFactory = java.security.cert.CertificateFactory.getInstance("X.509");
		String strCertificate = "-----BEGIN CERTIFICATE-----\n" + cert + "\n-----END CERTIFICATE-----\n";
		java.io.ByteArrayInputStream streamCertificate = new java.io.ByteArrayInputStream(strCertificate.getBytes("UTF-8"));
		return (java.security.cert.X509Certificate) certificateFactory.generateCertificate(streamCertificate);
	}
	
	// NET::ERR_CERT_WEAK_SIGNATURE_ALGORITHM
	public static void main(String[] args) throws Exception {
		X509CertImpl cert = (X509CertImpl) fromString(correctCert);
		sun.security.x509.SubjectKeyIdentifierExtension ext = (SubjectKeyIdentifierExtension) cert.getExtension(new ObjectIdentifier("2.5.29.14"));
		System.out.println(ext);
//		System.out.println(cert);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	static String correctCert = "MIIDxjCCAq6gAwIBAgIQY7CuAojCH5FEODdzUsv9izANBgkqhkiG9w0BAQsFADBn\n" + 
			"MSswKQYDVQQLDCJDcmVhdGVkIGJ5IGh0dHA6Ly93d3cuZmlkZGxlcjIuY29tMRUw\n" + 
			"EwYDVQQKDAxET19OT1RfVFJVU1QxITAfBgNVBAMMGERPX05PVF9UUlVTVF9GaWRk\n" + 
			"bGVyUm9vdDAeFw0xOTA1MTcxODAxNTVaFw0yNTA1MTYxODAxNTVaMFgxKzApBgNV\n" + 
			"BAsMIkNyZWF0ZWQgYnkgaHR0cDovL3d3dy5maWRkbGVyMi5jb20xFTATBgNVBAoM\n" + 
			"DERPX05PVF9UUlVTVDESMBAGA1UEAwwJKi4xNjMuY29tMIIBIjANBgkqhkiG9w0B\n" + 
			"AQEFAAOCAQ8AMIIBCgKCAQEAtBFaRMVyHqDnJ3f/n6NdLRNtoMjCWhFXG/4eXZK+\n" + 
			"81UeGah+76XS/QljqW0KF+zCaXej1mQwlRrFfth5re4Ds5tuvqty3aE+AotOfPTo\n" + 
			"Wnzz7E7j7LHbqSBG41EVT82y0QLs7Wk7g2DkxSpHvV7+mUxl+mR6t1owneZemlHX\n" + 
			"Uk3E/P2dgrkilPFSGOl+jIph8sHd8JcDCO7fERcJDWCyM5jFzFRAfq/82WMRzgqE\n" + 
			"s7C6U7pUmy+9i4q1IrsFBXFReCd6Cy4X7F3eO347HTTXpu22tDolcl/RyKPev+Hl\n" + 
			"u0ldqR3h82dn91TE5YAwWHGoXcgEzt7AHP5aWHuQsPgeEwIDAQABo30wezAOBgNV\n" + 
			"HQ8BAf8EBAMCBLAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwFAYDVR0RBA0wC4IJKi4x\n" + 
			"NjMuY29tMB8GA1UdIwQYMBaAFEx2jxm7NiOpT0njLGFay/lfc350MB0GA1UdDgQW\n" + 
			"BBTRyMHs0bo3i2ckoaAgfdpyekEa3jANBgkqhkiG9w0BAQsFAAOCAQEAGfrBp4MZ\n" + 
			"O1BbquDr2gXKENzbMZWikaiA0eQd5AuEqMqCabdIkkgc32xAjcYQCnYZk587oYpx\n" + 
			"jxepN0TaSFngxMkSdF5qesyhOR8Z1SrTyn13ygLgkz8ZH3QHXgAP/LF+KtaUkcbI\n" + 
			"BUc5srIA8tRIffu6J/QM8eBVGaSvPv5Fe1IrwjCWVP7r8RGDg+urC5AcSxuOL8Zo\n" + 
			"kKESV50nzDQNTO9BlOKk/s3SRyymBsIMNWnM6j9uKG0oArYNlRLB2TFb48iwkOtX\n" + 
			"sJ3rperFAZlrG0DRKmEQdUBDpg48U1ewtWVRJaviqg9ftXaszmbLKGUp7SQwoE8j\n" + 
			"2zdCC3I9rwRExg==\n";
	
	
}
