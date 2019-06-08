package org.hum.nettyproxy.http.codec;

import org.hum.nettyproxy.http.model.HttpRequest;
import org.hum.nettyproxy.http.util.HttpHelper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpRequestDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf byteBuf = (ByteBuf) msg;
    	// parse byteBuf to request
    	HttpRequest httpRequest = parse(byteBuf);
        ctx.fireChannelRead(httpRequest);
    }
    
    public HttpRequest parse(ByteBuf byteBuf) {
    	HttpRequest request = new HttpRequest();
    	// read request-line
    	request.setLine(HttpHelper.readLine(byteBuf));
    	
    	// parse to method
    	request.setMethod(request.getLine().split(" ")[0]);
    	
    	// read request-header
    	String line = null;
    	while (!(line = HttpHelper.readLine(byteBuf)).equals("")) {
    		int splitIndex = line.indexOf(":");
    		
    		if (splitIndex <= 0) {
    			continue;
    		}
    		
    		String key = line.substring(0, splitIndex).trim();
    		String value = line.substring(splitIndex + 1, line.length()).trim();
    		request.getHeaders().put(key, value);
    		
    		// parse to host and port
    		if ("host".equalsIgnoreCase(key)) {
    			if (value.contains(":")) {
    				String[] arr = value.split(":");
	    			request.setHost(arr[0]);
	    			request.setPort(Integer.parseInt(arr[1]));
    			} else {
					request.setHost(value);
					request.setPort("CONNECT".equalsIgnoreCase(request.getMethod()) ? 443 : 80);
    			}
    		}
    	}
    	
    	// read request-body
    	StringBuilder body = new StringBuilder();
    	while (!(line = HttpHelper.readLine(byteBuf)).equals("")) {
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
