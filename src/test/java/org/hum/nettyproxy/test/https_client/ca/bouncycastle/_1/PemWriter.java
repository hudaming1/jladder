package org.hum.nettyproxy.test.https_client.ca.bouncycastle._1;

import java.io.IOException;
import java.io.Writer;
import java.security.SecureRandom;

import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.util.encoders.Base64;

public class PemWriter extends PEMWriter {
	private String provider;

	public PemWriter(Writer out) {
		super(out);
	}

	private void writeSsh2Encoded(byte[] bytes) throws IOException {
		char[] buf = new char[50];

		bytes = Base64.encode(bytes);

		for (int i = 0; i < bytes.length; i += buf.length) {
			int index = 0;

			while (index != buf.length) {
				if ((i + index) >= bytes.length) {
					break;
				}
				buf[index] = (char) bytes[i + index];
				index++;
			}
			this.write(buf, 0, index);
			this.newLine();
		}
	}

	@Override
	public void writeObject(Object arg0) throws IOException {
		if (arg0 instanceof Ssh2PublicKeyImpl) {
			Ssh2PublicKeyImpl key = (Ssh2PublicKeyImpl) arg0;
			switch (key.getType()) {
			case IETFSECSH:
				super.write(key.getTokens()[0]);
				this.newLine();
				writeSsh2Encoded(key.getEncoded());
				super.write(key.getTokens()[1]);
				break;

			default:
				super.write(key.getTokens()[0] + " ");
				super.write(new String(Base64.encode(key.getEncoded())));
				super.write(" " + key.getUser());
				break;
			}
			return;
		}
		super.writeObject(arg0);
	}
}
