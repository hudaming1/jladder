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
		
		String correctCert = "MIIDujCCAqKgAwIBAgILBAAAAAABD4Ym5g0wDQYJKoZIhvcNAQEFBQAwTDEgMB4G\n" + 
				"A1UECxMXR2xvYmFsU2lnbiBSb290IENBIC0gUjIxEzARBgNVBAoTCkdsb2JhbFNp\n" + 
				"Z24xEzARBgNVBAMTCkdsb2JhbFNpZ24wHhcNMDYxMjE1MDgwMDAwWhcNMjExMjE1\n" + 
				"MDgwMDAwWjBMMSAwHgYDVQQLExdHbG9iYWxTaWduIFJvb3QgQ0EgLSBSMjETMBEG\n" + 
				"A1UEChMKR2xvYmFsU2lnbjETMBEGA1UEAxMKR2xvYmFsU2lnbjCCASIwDQYJKoZI\n" + 
				"hvcNAQEBBQADggEPADCCAQoCggEBAKbPJA6+Lm8omUVCxKs+IVSbC9N/hHD6ErPL\n" + 
				"v4dfxn+G07IwXNb9rfF73OX4YJYJkhD10FPe+3t+c4isUoh7SqbKSaZeqKeMWhG8\n" + 
				"eoLrvozps6yWJQeXSpkqBy+0Hne/ig+1AnwblrjFuTosvNYSuetZfeLQBoZfXklq\n" + 
				"tTleiDTsvHgMCJiEbKjNS7SgfQx5TfC4LcshytVsW33hoCmEofnTlEnLJGKRILzd\n" + 
				"C9XZzPnqJworc5HGnRusyMvo4KD0L5CLTfuwNhv2GXqF4G3yYROIXJ/gkwpRl4pa\n" + 
				"zq+r1feqCapgvdzZX99yqWATXgAByUr6P6TqBwMhAo6CygPCm48CAwEAAaOBnDCB\n" + 
				"mTAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUm+IH\n" + 
				"V2ccHsBqBt5ZtJot39wZhi4wNgYDVR0fBC8wLTAroCmgJ4YlaHR0cDovL2NybC5n\n" + 
				"bG9iYWxzaWduLm5ldC9yb290LXIyLmNybDAfBgNVHSMEGDAWgBSb4gdXZxwewGoG\n" + 
				"3lm0mi3f3BmGLjANBgkqhkiG9w0BAQUFAAOCAQEAmYFThxxol4aR7OBKuEQLq4Gs\n" + 
				"J0/WwbgcQ3izDJr86iw8bmEbTUsp9Z8FHSbBuOmDAGJFtqkIk7mpM0sYmsL4h4hO\n" + 
				"291xNBrBVNpGP+DTKqttVCL1OmLNIG+6KYnX3ZHu01yiPqFbQfXf5WRDLenVOavS\n" + 
				"ot+3i9DAgBkcRcAtjOj4LaR0VknFBbVPFd5uRHg5h6h+u/N5GJG79G+dwfCMNYxd\n" + 
				"AfvDbbnvRG15RjF+Cv6pgsH/76tuIMRQyV+dTZsXjAzlAcmgQWpzU/qlULRuJQ/7\n" + 
				"TBj0/VLZjmmx6BEP3ojY+x1J96relc8geMJgEtslQIxq/H5COEBkEveegeGTLg==";
		System.out.println(fromString(badCert));
		System.out.println("==================================================================");
		System.out.println(fromString(correctCert));
	}
}
