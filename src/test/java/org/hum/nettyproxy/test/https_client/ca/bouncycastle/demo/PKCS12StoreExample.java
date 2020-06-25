package org.hum.nettyproxy.test.https_client.ca.bouncycastle.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

import javax.security.auth.x500.X500PrivateCredential;

/**
 * Example of the creation of a PKCS #12 store
 */
public class PKCS12StoreExample
{
    public static KeyStore createKeyStore()
        throws Exception
    {
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");

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
        store.setKeyEntry(endCredential.getAlias(), endCredential.getPrivateKey(), null, chain);

        return store;
    }
    
    public static void main(
        String[]    args)
        throws Exception
    {
        KeyStore store = createKeyStore();
        char[]   password = "storePassword".toCharArray();
        
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        store.store(bOut, password);

        store = KeyStore.getInstance("PKCS12", "BC");

        store.load(new ByteArrayInputStream(bOut.toByteArray()), password);

        Enumeration en = store.aliases();
        while (en.hasMoreElements())
        {
            String alias = (String)en.nextElement();
            System.out.println("found " + alias + ", isCertificate? " + store.isCertificateEntry(alias));
        }
    }
}
