package org.hum.nettyproxy.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;

public class ByteBufHelper {

	private static final byte RETURN_LINE = 10;
    
    public static String readLine(ByteBuf byteBuf) {
    	StringBuilder sbuilder = new StringBuilder();
    	
    	byte b = -1;
    	while (byteBuf.isReadable() && (b = byteBuf.readByte()) != RETURN_LINE) {
    		sbuilder.append((char)b);
    	}
    	
    	return sbuilder.toString().trim();
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
}
