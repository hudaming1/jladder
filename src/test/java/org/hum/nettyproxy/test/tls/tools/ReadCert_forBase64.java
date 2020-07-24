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
		System.out.println(cert);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	static String correctCert = "MIIGNDCCBRygAwIBAgIQBc8Jq30Lkb5JSrcS+nwOPjANBgkqhkiG9w0BAQsFADBN\n" + 
			"MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMScwJQYDVQQDEx5E\n" + 
			"aWdpQ2VydCBTSEEyIFNlY3VyZSBTZXJ2ZXIgQ0EwHhcNMTkwOTI1MDAwMDAwWhcN\n" + 
			"MjAxMjAxMTIwMDAwWjByMQswCQYDVQQGEwJDTjERMA8GA1UECBMIU2hhbmdoYWkx\n" + 
			"ODA2BgNVBAoTL1NoYW5naGFpIEJhaSBKaSBJbmZvcm1hdGlvbiBUZWNobm9sb2d5\n" + 
			"IENvLC4gTHRkMRYwFAYDVQQDDA0qLmppYW5zaHUuY29tMIIBIjANBgkqhkiG9w0B\n" + 
			"AQEFAAOCAQ8AMIIBCgKCAQEA4yeaQygBOKQuDen5mZ5CBS4w1MC2nUWyhbDmlJ/e\n" + 
			"dnqIXm1sjTHaqmVz9wk3cmDozQMlRLIWKpHi5SNjvZao4qdP7Run4E7YdgAtP4JY\n" + 
			"Pxk6P4cj8JRL2cc21+qGoTEWloKiVHFUGtrukIGmIKiWDJGupxVdM8wq9KQcF856\n" + 
			"rovDuCnJaqficpW0pqnEynaorfjV1o5CuF0vEklaWx6v8pUyalFHwwBRFKiv2LOQ\n" + 
			"teZR0isLUL3Ujk3kSWsAFNjax8SEXEyGZ71539/AVrX3PF6vA7C3LFpZREQi51RP\n" + 
			"04WaHH9XIJdvZsXYcCTNqz/6GbkKe6RaPC+O4IDmMeaLZwIDAQABo4IC6TCCAuUw\n" + 
			"HwYDVR0jBBgwFoAUD4BhHIIxYdUvKOeNRji0LOHG2eIwHQYDVR0OBBYEFLXd7+ld\n" + 
			"vsm/TO4b/ZWq0vkNPjwqMCUGA1UdEQQeMByCDSouamlhbnNodS5jb22CC2ppYW5z\n" + 
			"aHUuY29tMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYB\n" + 
			"BQUHAwIwawYDVR0fBGQwYjAvoC2gK4YpaHR0cDovL2NybDMuZGlnaWNlcnQuY29t\n" + 
			"L3NzY2Etc2hhMi1nNi5jcmwwL6AtoCuGKWh0dHA6Ly9jcmw0LmRpZ2ljZXJ0LmNv\n" + 
			"bS9zc2NhLXNoYTItZzYuY3JsMEwGA1UdIARFMEMwNwYJYIZIAYb9bAEBMCowKAYI\n" + 
			"KwYBBQUHAgEWHGh0dHBzOi8vd3d3LmRpZ2ljZXJ0LmNvbS9DUFMwCAYGZ4EMAQIC\n" + 
			"MHwGCCsGAQUFBwEBBHAwbjAkBggrBgEFBQcwAYYYaHR0cDovL29jc3AuZGlnaWNl\n" + 
			"cnQuY29tMEYGCCsGAQUFBzAChjpodHRwOi8vY2FjZXJ0cy5kaWdpY2VydC5jb20v\n" + 
			"RGlnaUNlcnRTSEEyU2VjdXJlU2VydmVyQ0EuY3J0MAwGA1UdEwEB/wQCMAAwggEE\n" + 
			"BgorBgEEAdZ5AgQCBIH1BIHyAPAAdgDuS723dc5guuFCaR+r4Z5mow9+X7By2IMA\n" + 
			"xHuJeqj9ywAAAW1mprwnAAAEAwBHMEUCIQCfdp7Rfr3Q8E74ed9Oz6dnyguiozSJ\n" + 
			"KJaplAUME/1JnwIgJZqgxy9hkPs59RfFcnKIneBaTyPAEPnRrjqufVm6XgIAdgCH\n" + 
			"db/nWXz4jEOZX73zbv9WjUdWNv9KtWDBtOr/XqCDDwAAAW1mprxwAAAEAwBHMEUC\n" + 
			"IFMWq6cPUsKMGtNeGRh8TlPCrHBzMMH5yZ5FowaG9sl8AiEAg3UFrTdw57NKd9lx\n" + 
			"NI7XArjD5xHpp00JPFtZR0tgHL8wDQYJKoZIhvcNAQELBQADggEBAI0Mkny6NDRz\n" + 
			"lqTak1sDkoTRHah7QO5xzhv+IBEw0x2ny/zqaVHGPNS1/wHxMgKoJvTKpIynWfbr\n" + 
			"xfsJzBIYarjRpoOjNpWc+srmvi291iAOrY20wtj+P49G8eVjBPxjfVc0cnepaA2I\n" + 
			"4KOHxul6FuGc0Odq8LUhSbYYVPQBE1wCqEYFOIa2pUm/Rjozkp+2PAM4wbaK2crL\n" + 
			"sfjvRpc9hAv5Ph1C3ebOPbwPYGnk/o6QGG4BuAzuW1YZkHNb8nwLrdALwyDBaGpK\n" + 
			"5uJrZzkWaTHp4ETK8er9DD4NgxI+s35NzpPys41Ko79ealaG5kXkJ6ExXarm/STH\n" + 
			"v2x/Maoln6w=";
	
	
}
