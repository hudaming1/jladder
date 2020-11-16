package io.netty.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class SendHttpRequest4Socket {
	
	private static final String HTTP_REQUEST = "POST /migrate/sms/internal/pda/stockTransfer/queryStockForUnshelve HTTP/1.1\n" + 
			"Host:localhost:10086\n" + 
			"deviceId:319446CA9CB80C9D2C971471CC811015230DFBEB\n" + 
			"Authorization:Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqaXV4aWFucWlhbzAwMyIsInVzZXJfbmFtZSI6ImppdXhpYW5xaWFvMDAzIiwiZXhwIjoxNjA3NjcwNjAzLCJpYXQiOjE2MDQ5OTIyMDN9.zSA9qSliPDPvcdDhKmpivHnSqNb-YOw_el_58gYvaa4\n" + 
			"deviceType:PDA\n" + 
			"appVersion:83\n" + 
			"Content-Type:application/json; charset=UTF-8\n" + 
			"Content-Length:1024\n" + 
			"Connection:Keep-Alive\n" + 
			"Accept-Encoding:gzip\n" + 
			"User-Agent:okhttp/3.11.0\n" + 
			"Proxy-Client-IP:wuliu-ocean-gateway.b22.missfresh.net\n" + 
			"X-User-Id:3016\n" + 
			"_WtMock:7,\n" + 
			"\n";
	private static final String HTTP_CONTENT = "{\"batchManageLevel\":true,\"locationCode\":\"\",\"materialCode\":\"1000740\",\"materialId\":null,\"onlyTemporary\":null}";
	
	private static final Random rand = new Random();
	
	private static byte[] gen(int len) {
		byte[] bytes = new byte[len];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) rand.nextInt(100);
		}
		return bytes;
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 10086);
		OutputStream outputStream = socket.getOutputStream();
		outputStream.write(HTTP_REQUEST.getBytes());
		outputStream.write(gen(1023));
		outputStream.write("\n".getBytes());
		outputStream.flush();
		socket.close();
		System.out.println("over");
	}
}
