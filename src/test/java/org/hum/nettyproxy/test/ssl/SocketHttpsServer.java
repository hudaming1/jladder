package org.hum.nettyproxy.test.ssl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class SocketHttpsServer {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		String ksName = "herong.jks";
		char ksPass[] = "HerongJKS".toCharArray();
		char ctPass[] = "My1stKey".toCharArray();
		try {
			// Setup the socket address
			// InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(),
			// config.getHttpsPort());

			// Initialise the HTTPS server
			// Initialise the keystore
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(ksName), ksPass);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, ctPass);

			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);

			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(8888);

			System.out.println("Server started:");
			printServerSocketInfo(s);

			// Listening to the port
			SSLSocket c = (SSLSocket) s.accept();
			printSocketInfo(c);

			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));

			BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));

			String m = r.readLine();

			w.write("HTTP/1.0 200 OK");
			w.newLine();
			w.write("Content-Type: text/html");
			w.newLine();
			w.newLine();
			w.write("<html><body>Hello world!</body></html>");
			w.newLine();
			w.flush();
			w.close();
			r.close();
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printSocketInfo(SSLSocket s) {
		System.out.println("Socket class: " + s.getClass());
		System.out.println("   Remote address = " + s.getInetAddress().toString());
		System.out.println("   Remote port = " + s.getPort());
		System.out.println("   Local socket address = " + s.getLocalSocketAddress().toString());
		System.out.println("   Local address = " + s.getLocalAddress().toString());
		System.out.println("   Local port = " + s.getLocalPort());
		System.out.println("   Need client authentication = " + s.getNeedClientAuth());
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = " + ss.getCipherSuite());
		System.out.println("   Protocol = " + ss.getProtocol());
	}

	private static void printServerSocketInfo(SSLServerSocket s) {
		System.out.println("Server socket class: " + s.getClass());
		System.out.println("   Socket address = " + s.getInetAddress().toString());
		System.out.println("   Socket port = " + s.getLocalPort());
		System.out.println("   Need client authentication = " + s.getNeedClientAuth());
		System.out.println("   Want client authentication = " + s.getWantClientAuth());
		System.out.println("   Use client mode = " + s.getUseClientMode());
	}
}
