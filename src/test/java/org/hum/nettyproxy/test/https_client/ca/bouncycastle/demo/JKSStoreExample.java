package org.hum.nettyproxy.test.https_client.ca.bouncycastle.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

import javax.security.auth.x500.X500PrivateCredential;

/**
 * Example of basic use of a KeyStore.
 */
public class JKSStoreExample
{
    public static char[] keyPassword = "keyPassword".toCharArray();
    
    public static KeyStore createKeyStore()
        throws Exception
    {
        KeyStore store = KeyStore.getInstance("JKS");

        // initialize
        store.load(null, null);
        
        X500PrivateCredential    rootCredential = Utils.createRootCredential();
        X500PrivateCredential    interCredential = Utils.createIntermediateCredential(rootCredential.getPrivateKey(), rootCredential.getCertificate());
        X500PrivateCredential    endCredential = Utils.createEndEntityCredential(interCredential.getPrivateKey(), interCredential.getCertificate());

        Certificate[]            chain = new Certificate[3];
        
        chain[0] = endCredential.getCertificate();
        chain[1] = interCredential.getCertificate();
        chain[2] = rootCredential.getCertificate();
        
        // set the entries
        store.setCertificateEntry(rootCredential.getAlias(), rootCredential.getCertificate());
        store.setKeyEntry(endCredential.getAlias(), endCredential.getPrivateKey(), keyPassword, chain);

        return store;
    }
    
    public static void main(
        String[]    args)
        throws Exception
    {
        KeyStore store = createKeyStore();
        char[]   password = "storePassword".toCharArray();
        
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        
        // save the store
        store.store(bOut, password);

        // reload from scratch
        store = KeyStore.getInstance("JKS");
        
        store.load(new ByteArrayInputStream(bOut.toByteArray()), null);

        Enumeration en = store.aliases();
        while (en.hasMoreElements())
        {
            String alias = (String)en.nextElement();
            System.out.println("found " + alias + ", isCertificate? " + store.isCertificateEntry(alias));
        }
    }
}
