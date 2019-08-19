package org.hum.nettyproxy.common.codec.http;

import java.util.ArrayList;
import java.util.List;

import org.hum.nettyproxy.common.model.HttpResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;

public class HttpResponseDecoder extends io.netty.handler.codec.http.HttpResponseDecoder {
	
	public HttpResponse decode(ByteBuf byteBuf) throws Exception {
		Object decodeObj = _decode(byteBuf);
		System.out.println(decodeObj);
		
		HttpResponse response = new HttpResponse();
//		
//		if (decodeObj instanceof DefaultHttpResponse) {
//    		DefaultHttpResponse resp = (DefaultHttpResponse) decodeObj;
//    		// System.out.println(resp.toString() + "\n" + byteBuf.toString(io.netty.util.CharsetUtil.UTF_8));
//    		response.setCode(resp.status().code());
//    		ByteBuf content = PooledByteBufAllocator.DEFAULT.buffer(byteBuf.readableBytes());
//    		byteBuf.readBytes(content);
//    		response.setContent(content);
//    	} else if (decodeObj instanceof DefaultHttpContent) {
//    		// http-chunked content ?
//    		DefaultHttpContent resp = (DefaultHttpContent) decodeObj;
//    		ByteBuf buf = resp.content();
//    		response.setCode(200);
//    		response.setContent(buf);
//    		// System.out.println(resp.toString() + "\n" + buf.toString(io.netty.util.CharsetUtil.UTF_8));
//    	} 
    	return response;
	}
	
	private Object _decode(ByteBuf byteBuf) throws Exception {
		List<Object> list = new ArrayList<Object>();
		super.decode(null, byteBuf, list);
		return list.get(0);
	}
}