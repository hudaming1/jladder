package org.hum.nettyproxy.adapter.http.codec;

import org.hum.nettyproxy.adapter.http.model.HttpRequest;
import org.hum.nettyproxy.adapter.http.util.ByteBufHelper;
import org.hum.nettyproxy.common.Constant;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * HTTP请求解码器
 * @author hudaming
 */
public class HttpRequestDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf byteBuf = (ByteBuf) msg;
        ctx.fireChannelRead(parse(byteBuf));
    }
    
    public HttpRequest parse(ByteBuf byteBuf) {
    	HttpRequest request = new HttpRequest();
    	// read request-line
    	request.setLine(ByteBufHelper.readLine(byteBuf));
    	
    	// parse to method
    	request.setMethod(request.getLine().split(" ")[0]);
    	
    	// read request-header
    	String line = null;
    	while (!(line = ByteBufHelper.readLine(byteBuf)).equals("")) {
    		int splitIndex = line.indexOf(":");
    		
    		if (splitIndex <= 0) {
    			continue;
    		}
    		
    		String key = line.substring(0, splitIndex).trim();
    		String value = line.substring(splitIndex + 1, line.length()).trim();
    		request.getHeaders().put(key, value);
    		
    		// 从HTTP请求头中摘除代理痕迹
    		if (Constant.HTTP_PROXY_HEADER.equals(key)) {
    			continue;
    		}
    		
    		// parse to host and port
    		if (Constant.HTTP_HOST_HEADER.equalsIgnoreCase(key)) {
    			if (value.contains(":")) {
    				String[] arr = value.split(":");
	    			request.setHost(arr[0]);
	    			request.setPort(Integer.parseInt(arr[1]));
    			} else {
					request.setHost(value);
					request.setPort(Constant.HTTPS_METHOD.equalsIgnoreCase(request.getMethod()) ? Constant.DEFAULT_HTTPS_PORT : Constant.DEFAULT_HTTP_PORT);
    			}
    		}
    	}
    	
    	// read request-body
    	StringBuilder body = new StringBuilder();
    	while (!(line = ByteBufHelper.readLine(byteBuf)).equals("")) {
    		body.append(line);
    	}
    	request.setBody(body.toString());
    	
    	// reference ByteBuf
    	request.setByteBuf(byteBuf);
    	
    	// reset bytebuf read_size, ensure readable
    	byteBuf.resetReaderIndex();
    	
    	return request;
    }
}
