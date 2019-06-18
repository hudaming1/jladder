package org.hum.nettyproxy.common.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * TODO 
 * @author huming
 */
public class ByteBufBuilder {

	private ByteBuf byteBuf;
	
	public ByteBufBuilder(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public ByteBufBuilder readFile(File file) throws FileNotFoundException, IOException {
		return this;
	}
	
	public ByteBufBuilder replace(String src, String target) {
		return this;
	}
	
	public ByteBuf get() {
		return this.byteBuf;
	}
}
