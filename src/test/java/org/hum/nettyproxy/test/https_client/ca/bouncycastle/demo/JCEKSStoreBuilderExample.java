package org.hum.nettyproxy.test.https_client.ca.bouncycastle.demo;

import java.security.KeyStore;

/**
 * Basic example of use of KeyStore.Builder to create an object that
 * can be used recover a private key.
 */
public class JCEKSStoreBuilderExample
{   
    public static void main(
        String[]    args)
        throws Exception
    {
        KeyStore store = JCEKSStoreEntryExample.createKeyStore();
        
        char[]   password = "storePassword".toCharArray();
        
        // create the builder
        KeyStore.Builder builder = KeyStore.Builder.newInstance(store, new KeyStore.PasswordProtection(JCEKSStoreEntryExample.keyPassword));
        
        // use the builder to recover the KeyStore and obtain the key
        store = builder.getKeyStore();
        
        KeyStore.ProtectionParameter param = builder.getProtectionParameter(Utils.END_ENTITY_ALIAS);

        KeyStore.Entry entry = store.getEntry(Utils.END_ENTITY_ALIAS, param);

        System.out.println("recovered " + entry.getClass());
    }
}
