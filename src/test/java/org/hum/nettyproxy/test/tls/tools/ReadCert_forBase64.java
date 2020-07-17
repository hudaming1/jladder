package org.hum.nettyproxy.test.tls.tools;

public class ReadCert_forBase64 {
	static public java.security.cert.X509Certificate fromString(String cert) throws Exception {
		java.security.cert.CertificateFactory certificateFactory = java.security.cert.CertificateFactory.getInstance("X.509");
		String strCertificate = "-----BEGIN CERTIFICATE-----\n" + cert + "\n-----END CERTIFICATE-----\n";
		java.io.ByteArrayInputStream streamCertificate = new java.io.ByteArrayInputStream(strCertificate.getBytes("UTF-8"));
		return (java.security.cert.X509Certificate) certificateFactory.generateCertificate(streamCertificate);
	}
	
	public static void main(String[] args) throws Exception {
		
		String correctCert = "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";
		System.out.println(fromString(correctCert));
	}
}
