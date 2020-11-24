package org.jladder.common.codec.http;

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

public class HttpResponseConverter {
	
	public org.jladder.common.model.HttpResponse decode(HttpResponse response) throws Exception {
		if (response instanceof DefaultHttpResponse) {
    		DefaultHttpResponse resp = (DefaultHttpResponse) response;
    		org.jladder.common.model.HttpResponse decResponse = new org.jladder.common.model.HttpResponse();
    		decResponse.setCode(resp.status().code());
			decResponse.setHeaders(getHeaders(resp));
    		return decResponse;
    	} 
    	return null;
	}
	
	private Map<String, String> getHeaders(DefaultHttpResponse resp) {
		Map<String, String> headers = new HashMap<String, String>();
		resp.headers().forEach(header -> {
			headers.put(header.getKey(), header.getValue());
		});
		return headers;
	}
}