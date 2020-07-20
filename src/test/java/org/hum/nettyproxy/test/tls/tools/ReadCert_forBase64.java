package org.hum.nettyproxy.test.tls.tools;

public class ReadCert_forBase64 {
	static public java.security.cert.X509Certificate fromString(String cert) throws Exception {
		java.security.cert.CertificateFactory certificateFactory = java.security.cert.CertificateFactory.getInstance("X.509");
		String strCertificate = "-----BEGIN CERTIFICATE-----\n" + cert + "\n-----END CERTIFICATE-----\n";
		java.io.ByteArrayInputStream streamCertificate = new java.io.ByteArrayInputStream(strCertificate.getBytes("UTF-8"));
		return (java.security.cert.X509Certificate) certificateFactory.generateCertificate(streamCertificate);
	}
	
	public static void main(String[] args) throws Exception {
		
		String correctCert = "MIIDlTCCAX0CCQDshwr9aw7MlTANBgkqhkiG9w0BAQUFADCBhjELMAkGA1UEBhMC\n" + 
				"Q04xEDAOBgNVBAgMB1NoYWFuWGkxEzARBgNVBAoMCk5pY2tMaSBMdGQxFjAUBgNV\n" + 
				"BAsMDU5pY2tMaSBMdGQgQ0ExFzAVBgNVBAMMDk5pY2tMaSBSb290IENBMR8wHQYJ\n" + 
				"KoZIhvcNAQkBFhBsamZwb3dlckAxNjMuY29tMB4XDTIwMDUzMTA0MjgyNVoXDTMw\n" + 
				"MDUyOTA0MjgyNVowFjEUMBIGA1UEAwwLKi5iYWlkdS5jb20wgZ8wDQYJKoZIhvcN\n" + 
				"AQEBBQADgY0AMIGJAoGBAK75o/PWdxMf53Y/pLSGosvO2RnnN6XAZyDDkv4ujqxB\n" + 
				"hna/YkL6kkJlpPiDKqflq/9gAkcyvMqHpHmEjAO0xHT5mAe1B1f8ogCOdg7XcNh1\n" + 
				"3rjSZiYRZ+TuyMASUPt6ZdPLnnT3IhqwguV4t6aCFYZM/9u0wX0oWppIe6ey9I03\n" + 
				"AgMBAAEwDQYJKoZIhvcNAQEFBQADggIBADXMFg38piDerKGIbe6q6LdeFYSWIfyv\n" + 
				"/dBB7fp0XEuSypq2N6WM3fr3tQnSArUkg7XWtQnj6GGxmrmSJhRehSQf87S4KSuq\n" + 
				"9vxaEW4ToCTPteW8YtvGBJUP1nyOZcU260YfINlPtoLsY9dirbPM57P1litgmIXI\n" + 
				"EJog/B4EjhftaQDWUdqFsN9nAXl4LDGaKSGCbDn6i8K/juPVpiNb2/Oqk82bBuod\n" + 
				"vDL1Aa8SfaKzAZo0Q3L6/jgwCKYm2yekx8aBWu+Mrk5zbLpvoiIRNeAMPQYfnahX\n" + 
				"PfvSEm94hqMGEPcYltA2u095kGSNQnIIX06EmStpXt25HvyUpkqzKPEN03vgGMAm\n" + 
				"ri+MQ5O5zLP2LNE9f6K0HsdaUx5vLK5Uapxsi6i0Y/bZwdrY7rx4VlIg2h+dlwQP\n" + 
				"zWWX2UxnQahHIcymWvajI3UchbIo2nqC7cSXqfaxank+u/um6SzZ9+faw4L1Kw9t\n" + 
				"nK06v5npOMu3Bt4fgPqSCEvuuBaGt4kNXsCw1KvxvB0yZyevF257nsn+iAYGObwt\n" + 
				"bsjxdq+ShvCtmzLrAihNl6DiVFgRVkiqZoemWyjoWpvmb0dWYIvN/Tu77wDlT1b/\n" + 
				"7Muz2MluNPxjRecj4rlYNzin7t6C5lA5+ZTN37bzxW3MIikfThTdQXCJiWta/yMG\n" + 
				"fBdpqJZ47NLS\n";
		System.out.println(fromString(correctCert));
	}
}
