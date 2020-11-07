package org.hum.jladder.common.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * TODO 
 * @author huming
 */
public class HttpResponseByteBufBuilder {

	private ByteBuf byteBuf;
	
	public HttpResponseByteBufBuilder(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public HttpResponseByteBufBuilder readFile(File file) throws FileNotFoundException, IOException {
		return this;
	}
	
	public HttpResponseByteBufBuilder replace(String src, String target) {
		return this;
	}
	
	public ByteBuf get() {
		return this.byteBuf;
	}
}
