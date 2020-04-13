//package org.hum.nettyproxy.test.ssl.test1;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.net.Socket;
//import java.security.Key;
//import java.security.SecureRandom;
//
//import io.netty.util.internal.SocketUtils;
//
//public class HttpsMockClient extends HttpsMockBase {
//	static DataInputStream in;
//	static DataOutputStream out;
//	static Key key;
//
//	public static void main(String args[]) throws Exception {
//		int port = 80;
//		Socket s = new Socket("localhost", port);
//		s.setReceiveBufferSize(102400);
//		s.setKeepAlive(true);
//		in = new DataInputStream(s.getInputStream());
//		out = new DataOutputStream(s.getOutputStream());
//		shakeHands();
//
//		System.out.println("------------------------------------------------------------------");
//		String name = "duck";
//		writeBytes(name.getBytes());
//
//		int len = in.readInt();
//		byte[] msg = readBytes(len);
//		System.out.println("服务器反馈消息:" + byte2hex(msg));
//		Thread.sleep(1000 * 100);
//
//	}
//
//	private static void shakeHands() throws Exception {
//		// 第一步 客户端发送自己支持的hash算法
//		String supportHash = "SHA1";
//		int length = supportHash.getBytes().length;
//		out.writeInt(length);
//		SocketUtils.writeBytes(out, supportHash.getBytes(), length);
//
//		// 第二步 客户端验证服务器端证书是否合法
//		int skip = in.readInt();
//		byte[] certificate = SocketUtils.readBytes(in, skip);
//		java.security.cert.Certificate cc = CertifcateUtils.createCertiface(certificate);
//
//		publicKey = cc.getPublicKey();
//		cc.verify(publicKey);
//		System.out.println("客户端校验服务器端证书是否合法：" + true);
//
//		// 第三步 客户端校验服务器端发送过来的证书成功,生成随机数并用公钥加密
//		System.out.println("客户端校验服务器端发送过来的证书成功,生成随机数并用公钥加密");
//		SecureRandom seed = new SecureRandom();
//		int seedLength = 2;
//		byte seedBytes[] = seed.generateSeed(seedLength);
//		System.out.println("生成的随机数为 : " + byte2hex(seedBytes));
//		System.out.println("将随机数用公钥加密后发送到服务器");
//		byte[] encrptedSeed = encryptByPublicKey(seedBytes, null);
//		SocketUtils.writeBytes(out, encrptedSeed, encrptedSeed.length);
//
//		System.out.println("加密后的seed值为 :" + byte2hex(encrptedSeed));
//
//		String message = random();
//		System.out.println("客户端生成消息为:" + message);
//
//		System.out.println("使用随机数并用公钥对消息加密");
//		byte[] encrpt = encryptByPublicKey(message.getBytes(), seed);
//		System.out.println("加密后消息位数为 : " + encrpt.length);
//		SocketUtils.writeBytes(out, encrpt, encrpt.length);
//
//		System.out.println("客户端使用SHA1计算消息摘要");
//		byte hash[] = cactHash(message.getBytes());
//		System.out.println("摘要信息为:" + byte2hex(hash));
//
//		System.out.println("消息加密完成，摘要计算完成，发送服务器");
//		SocketUtils.writeBytes(out, hash, hash.length);
//
//		System.out.println("客户端向服务器发送消息完成，开始接受服务器端发送回来的消息和摘要");
//		System.out.println("接受服务器端发送的消息");
//		int serverMessageLength = in.readInt();
//		byte[] serverMessage = SocketUtils.readBytes(in, serverMessageLength);
//		System.out.println("服务器端的消息内容为 ：" + byte2hex(serverMessage));
//
//		System.out.println("开始用之前生成的随机密码和DES算法解密消息,密码为:" + byte2hex(seedBytes));
//		byte[] desKey = DesCoder.initSecretKey(new SecureRandom(seedBytes));
//		key = DesCoder.toKey(desKey);
//
//		byte[] decrpytedServerMsg = DesCoder.decrypt(serverMessage, key);
//		System.out.println("解密后的消息为:" + byte2hex(decrpytedServerMsg));
//
//		int serverHashLength = in.readInt();
//		byte[] serverHash = SocketUtils.readBytes(in, serverHashLength);
//		System.out.println("开始接受服务器端的摘要消息:" + byte2hex(serverHash));
//
//		byte[] serverHashValues = cactHash(decrpytedServerMsg);
//		System.out.println("计算服务器端发送过来的消息的摘要 : " + byte2hex(serverHashValues));
//
//		System.out.println("判断服务器端发送过来的hash摘要是否和计算出的摘要一致");
//		boolean isHashEquals = byteEquals(serverHashValues, serverHash);
//
//		if (isHashEquals) {
//			System.out.println("验证完成，握手成功");
//		} else {
//			System.out.println("验证失败，握手失败");
//		}
//	}
//
//	public static byte[] readBytes(int length) throws Exception {
//		byte[] undecrpty = SocketUtils.readBytes(in, length);
//		System.out.println("读取未解密消息:" + byte2hex(undecrpty));
//		return DesCoder.decrypt(undecrpty, key);
//	}
//
//	public static void writeBytes(byte[] data) throws Exception {
//		byte[] encrpted = DesCoder.encrypt(data, key);
//		System.out.println("写入加密后消息:" + byte2hex(encrpted));
//		SocketUtils.writeBytes(out, encrpted, encrpted.length);
//	}
//}