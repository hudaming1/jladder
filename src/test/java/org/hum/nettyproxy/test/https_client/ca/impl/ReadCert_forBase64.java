package org.hum.nettyproxy.test.https_client.ca.impl;

public class ReadCert_forBase64 {
	static public java.security.cert.X509Certificate fromString(String cert) throws Exception {
		java.security.cert.CertificateFactory certificateFactory = java.security.cert.CertificateFactory.getInstance("X.509");
		String strCertificate = "-----BEGIN CERTIFICATE-----\n" + cert + "\n-----END CERTIFICATE-----\n";
		java.io.ByteArrayInputStream streamCertificate = new java.io.ByteArrayInputStream(strCertificate.getBytes("UTF-8"));
		return (java.security.cert.X509Certificate) certificateFactory.generateCertificate(streamCertificate);
	}
	
	public static void main(String[] args) throws Exception {
		String badCert = "MIIDkDCCAXgCBEtDW4kwDQYJKoZIhvcNAQEFBQAwgYYxCzAJBgNVBAYTAkNOMRAw\n" + 
				"DgYDVQQIEwdTaGFhblhpMRMwEQYDVQQKEwpOaWNrTGkgTHRkMRYwFAYDVQQLEw1O\n" + 
				"aWNrTGkgTHRkIENBMRcwFQYDVQQDEw5OaWNrTGkgUm9vdCBDQTEfMB0GCSqGSIb3\n" + 
				"DQEJARYQbGpmcG93ZXJAMTYzLmNvbTAeFw0yMDA2MTkwOTAyMTZaFw0yMDA2MjYx\n" + 
				"NjE0MTZaMBYxFDASBgNVBAMMCyouYmFpZHUuY29tMIGfMA0GCSqGSIb3DQEBAQUA\n" + 
				"A4GNADCBiQKBgQCw/N47BT4cfYqT9VwRl1aLzkg/T+ICva1exBu8m11EqH7xwQjP\n" + 
				"vvhKenf5RdiTeJ6cqxuk5B1Fm/xtQXMvomne6nLUXI1GJVmUKX+Wj/oYXWVcitS5\n" + 
				"N5wvo2FK0wx1UM4Qud2Pi5RwRlaVQOPLD9OxHhMeaaGZR3+HdBuKeDG93QIDAQAB\n" + 
				"MA0GCSqGSIb3DQEBBQUAA4ICAQAubi0WiUt5mYYTkQZc+1DMYCrM4kiCtpaRUZZ3\n" + 
				"ha06E2xWeneGQBNUnK1q0+tKFdKbLNNIDu/5MXVZP9Vy2H2RnPoAWPLdvMAAOuVY\n" + 
				"A6l8m5kfrKYIRyYTmTbf/g3oTZqKN3GPEfoQKYzo5RxGQahUTPurZ9KkCq7W12VC\n" + 
				"gwJM5eZETW5uG7pg+yNCfk7a2NGw+LSmCEwY9ztM9W6pxe9p9qaKyOTbmwKtGI51\n" + 
				"Tm/9jhTf5ZRvSpTo+89MtmNSaLXJN1nKFyanlX5KHYRCgy3QGem4GAQYJ5p2XJze\n" + 
				"IalK6UZHiou+pMYzLHy24tqnPlUEl5Wxi9zK8mjFNFgaxFbi8IFDVY3B/ViKfrPe\n" + 
				"zRZmX1tI4BqSbxPAfkHpJ60Nb0RhwZrUxehDTNFZvYiTi+ra73ZokXwc8/NJT/Ry\n" + 
				"3hEh/gOUESpePnE96Spz9VYuLFbPKlCFw+bM+ZLXSe6pnGiQShYdBZlDT6H+X5mT\n" + 
				"ee44yXeUmjEe1TFQkq7xWXO9pSQcKJPLcUnVqiY5nfrJHLkk8GH8ZDmyI/pJQZNv\n" + 
				"GhTQqpfON7KtRcoEnLj6zTXID/w1CnaXNq02bbc76M11k2oYq8jTRQKLdatmNkSl\n" + 
				"K476sYF80GUR/Aj60vba7Qh5wAlzEIklN3IRE1lyLltjah/eCk2s9iBoBUsLgjDC\n" + 
				"jpnvkg==";
		
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
				"fBdpqJZ47NLS";
		System.out.println(fromString(badCert));
		System.out.println("==================================================================");
		 System.out.println(fromString(correctCert));
	}
}
