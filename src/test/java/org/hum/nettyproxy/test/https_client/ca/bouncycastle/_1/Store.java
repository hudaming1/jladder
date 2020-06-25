package org.hum.nettyproxy.test.https_client.ca.bouncycastle._1;


public interface Store<T> {
	public void save(T obj, String password) throws Exception;

	public T read() throws StorageException;
}
