package org.hum.nettyproxy.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Test {

	public static void main(String[] args) throws UnknownHostException, IOException {
		@SuppressWarnings("resource")
		Socket socket = new Socket("127.0.0.1", 5432);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
		bw.write("test");
		bw.flush();
		
		System.out.println("flush over");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line = null;
		while (!(line = br.readLine()).equals("")) {
			System.out.println(line);
		}
		System.out.println(br.readLine());
		System.out.println("ok");
	}
}
