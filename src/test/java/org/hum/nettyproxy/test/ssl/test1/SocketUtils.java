package org.hum.nettyproxy.test.ssl.test1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketUtils {
	public static void close(Socket s) {
		try {
			s.shutdownInput();
			s.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] readBytes(DataInputStream in, int length) throws IOException {
		int r = 0;
		byte[] data = new byte[length];
		while (r != -1) {
			r += in.read(data, r, length - r);
		}
		return data;

	}

	public static void writeBytes(DataOutputStream out, byte[] bytes, int length) throws IOException {
		out.writeInt(length);
		out.write(bytes, 0, length);
		out.flush();
	}
}