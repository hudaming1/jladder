package org.hum.nettyproxy.adapter.http.util;

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
}
