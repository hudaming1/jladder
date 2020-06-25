package org.hum.nettyproxy.test.https_client.ca.bouncycastle._1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

public class Ssh2PublicKeyImpl implements RSAPublicKey {

	private RSAPublicKey key;

	private SSH2_KEY_TYPE type;

	private String user;

	public Ssh2PublicKeyImpl(RSAPublicKey key) {
		this(key, SSH2_KEY_TYPE.OPENSSH);
	}

	public Ssh2PublicKeyImpl(RSAPublicKey key, SSH2_KEY_TYPE type, String user) {
		super();
		this.key = key;
		this.type = type;
		this.user = user;
	}

	public Ssh2PublicKeyImpl(RSAPublicKey key, SSH2_KEY_TYPE type) {
		super();
		this.key = key;
		this.type = type;
	}

	public BigInteger getModulus() {
		return key.getModulus();
	}

	public BigInteger getPublicExponent() {
		return key.getPublicExponent();
	}

	public String getAlgorithm() {
		return key.getAlgorithm();
	}

	public String getFormat() {
		return key.getFormat();
	}

	public byte[] getEncoded() {
		try {
			return encodePublicKey(key);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] encodePublicKey(RSAPublicKey key) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		/* encode the "ssh-rsa" string */
		byte[] sshrsa = new byte[] { 0, 0, 0, 7, 's', 's', 'h', '-', 'r', 's', 'a' };
		out.write(sshrsa);
		/* Encode the public exponent */
		BigInteger e = key.getPublicExponent();
		byte[] data = e.toByteArray();
		encodeUInt32(data.length, out);
		out.write(data);
		/* Encode the modulus */
		BigInteger m = key.getModulus();
		data = m.toByteArray();
		encodeUInt32(data.length, out);
		out.write(data);
		return out.toByteArray();
	}

	private void encodeUInt32(int value, OutputStream out) throws IOException {
		byte[] tmp = new byte[4];
		tmp[0] = (byte) ((value >>> 24) & 0xff);
		tmp[1] = (byte) ((value >>> 16) & 0xff);
		tmp[2] = (byte) ((value >>> 8) & 0xff);
		tmp[3] = (byte) (value & 0xff);
		out.write(tmp);
	}

	public SSH2_KEY_TYPE getType() {
		return type;
	}

	public String[] getTokens() {
		return type.getTokens();
	}

	public String getUser() {
		return user;
	}

}
