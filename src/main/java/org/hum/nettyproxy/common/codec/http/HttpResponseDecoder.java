package org.hum.nettyproxy.common.codec.http;

import java.util.ArrayList;
import java.util.List;

import org.hum.nettyproxy.common.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.util.CharsetUtil;

public class HttpResponseDecoder extends io.netty.handler.codec.http.HttpResponseDecoder {
	
	private final Logger logger = LoggerFactory.getLogger(HttpResponseDecoder.class);
	
	public HttpResponse decode(ByteBuf byteBuf) throws Exception {
		byteBuf.resetReaderIndex();
//		byteBuf.discardReadBytes();
//		byteBuf.resetReaderIndex();
//		byte[] bytes = new byte[61]; 
//		byteBuf.readBytes(bytes);
		/**
		 * 访问http://www.mingyihui.net/api_doctorsite.php?mode=getLoginUserInfo网址后（内容少，仅有一个chunked）
		 * byteBuf cap=1024, widx=600，decode后，ridx=500?，剩下字节实为HttpContent（chunked导致）
		 * 目前需要解析出HttpContent内容(chunked格式参考：https://img-my.csdn.net/uploads/201103/10/0_1299726886j0Qg.gif)
		 * 1.先解析第一行，读出\r\b前的内容，理论是一个16进制的字符串，这个字符串为chunkedContent的长度
		 * 2.长度从16进制转成10进制，根据长度读取后面的内容
		 * 3.当遇到新的chunked包，开头是0时，代表整个传输结束
		 */
		byteBuf.toString(CharsetUtil.UTF_8);
		Object decodeObj = _decode(byteBuf);
		if (decodeObj == null) {
			return null;
		}
		
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
		if (list == null || list.isEmpty()) {
			logger.warn("can't parse response bytebuf");
			return null;
		}
		return list.get(0);
	}
}