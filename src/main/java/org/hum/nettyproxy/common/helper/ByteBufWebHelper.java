package org.hum.nettyproxy.common.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hum.nettyproxy.adapter.http.simpleserver.NettySimpleServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufWebHelper {

	private static final Logger logger = LoggerFactory.getLogger(ByteBufWebHelper.class);
	private static final byte RETURN_LINE = 10;

	public static String readLine(ByteBuf byteBuf) {
		StringBuilder sbuilder = new StringBuilder();

		byte b = -1;
		while (byteBuf.isReadable() && (b = byteBuf.readByte()) != RETURN_LINE) {
			sbuilder.append((char) b);
		}

		return sbuilder.toString().trim();
	}

	private static String WEB_ROOT;
	private static ByteBuf _404ByteBuf;
	private static ByteBuf _500ByteBuf;

	static {
		try {
			WEB_ROOT = NettySimpleServerHandler.class.getClassLoader().getResource("").toURI().getPath();
			WEB_ROOT += "webapps";

			_404ByteBuf = ByteBufWebHelper.readFile(Unpooled.directBuffer(), new File(WEB_ROOT + "/404.html"));
			_500ByteBuf = ByteBufWebHelper.readFile(Unpooled.directBuffer(), new File(WEB_ROOT + "/500.html"));
		} catch (Exception e) {
			WEB_ROOT = "";
			logger.error("init netty-simple-http-server error, can't init web-root-path", e);
		}
	}
	
	public static String getWebRoot() {
		return WEB_ROOT;
	}
	
	public static ByteBuf _404ByteBuf() {
		return _404ByteBuf;
	}
	
	public static ByteBuf _500ByteBuf() {
		return _500ByteBuf;
	}

	public static ByteBuf readFileFromWebapps(ByteBuf byteBuf, String filePath) throws IOException {

		return readFile(byteBuf, new File(WEB_ROOT + "/" + filePath));
	}
	
	public static ByteBuf readFile(ByteBuf byteBuf, File file) throws IOException {
		BufferedInputStream fileInputStream = null;
		try {
			fileInputStream = new BufferedInputStream(new FileInputStream(file));
			int read = -1;
			while ((read = fileInputStream.read()) != -1) {
				byteBuf.writeByte((byte) read);
			}
			return byteBuf;
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
	}

	public static String readFile2String(File file) throws FileNotFoundException, IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			StringBuilder sbuilder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sbuilder.append(line);
			}
			return sbuilder.toString();
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
}
