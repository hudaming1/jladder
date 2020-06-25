package org.hum.nettyproxy.test.https_client.ca.bouncycastle.demo;

import java.io.FileOutputStream;
import java.security.KeyStore;

/**
 * Create some keystore files in the current directory.
 */
public class KeyStoreFileUtility
{
    public static void main(
        String[]    args)
        throws Exception
    {
        char[]   password = "storePassword".toCharArray();

        // create and save a JKS store
        KeyStore store = JKSStoreExample.createKeyStore();
        
        store.store(new FileOutputStream("keystore.jks"), password);

        // create and save a PKCS #12 store
        store = PKCS12StoreExample.createKeyStore();
        
        store.store(new FileOutputStream("keystore.p12"), password);
    }
}
