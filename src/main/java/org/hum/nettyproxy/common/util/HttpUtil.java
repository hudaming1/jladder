package org.hum.nettyproxy.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.helper.ByteBufWebHelper;
import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.buffer.ByteBuf;

public class HttpUtil {
	
	private static final byte[] _2_ReturnLine = (Constant.RETURN_LINE + Constant.RETURN_LINE).getBytes();

	/**
	 * 将HTTP请求中的Body Form参数解析成Map
	 * @param body
	 * @return
	 */
	public static Map<String, String> parseBody2FormData(String body) {
		if (body == null || body.trim().isEmpty()) {
			return Collections.emptyMap();
		}
		String[] kvDatas = body.split("&");
		if (kvDatas == null || kvDatas.length == 0) {
			return Collections.emptyMap();
		}
		Map<String, String> params = new HashMap<String, String>();
		for (String kvdata : kvDatas) {
			if (kvdata == null || kvdata.isEmpty()) {
				continue;
			}
			String[] param = kvdata.split("=");
			if (param == null || param.length != 2) {
				continue;
			}
			params.put(param[0], param[1]);
		}
		return params;
	}

    /**
     * ByteBuf -> HttpRequest
     * @param byteBuf
     * @return
     */
    public static HttpRequest decode(ByteBuf byteBuf) {
    	HttpRequest request = new HttpRequest();
    	// read request-line
    	request.setLine(ByteBufWebHelper.readLine(byteBuf));
    	
    	// parse to method
    	request.setMethod(request.getLine().split(" ")[0]);
    	
    	// read request-header
    	String line = null;
    	while (!(line = ByteBufWebHelper.readLine(byteBuf)).equals("")) {
    		int splitIndex = line.indexOf(":");
    		
    		if (splitIndex <= 0) {
    			continue;
    		}
    		
    		String key = line.substring(0, splitIndex).trim();
    		String value = line.substring(splitIndex + 1, line.length()).trim();
    		request.getHeaders().put(key, value);
    		
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
    	while (!(line = ByteBufWebHelper.readLine(byteBuf)).equals("")) {
    		body.append(line);
    	}
    	request.setBody(body.toString());
    	
    	// reference ByteBuf
    	request.setByteBuf(byteBuf);
    	
    	// fixbug: 有些服务器要求比较严格，目前看多几换行比少换行更能有效的访问到正确的URL，如果删除这段\r\n\r\n代码，再访问这个URL就会没有响应：http://pos.baidu.com/auto_dup?psi=2825367ec24f0f2315cbfc2e69f5a2c0&di=0&dri=0&dis=0&dai=0&ps=0&enu=encoding&dcb=___baidu_union_callback_&dtm=AUTO_JSONP&dvi=0.0&dci=-1&dpt=none&tsr=0&tpr=1560287949982&ti=%E8%BF%993%E4%B8%AA%E6%9C%89%E5%85%B3%E6%8A%A4%E8%82%A4%E7%9A%84%E5%B0%8F%E7%AA%8D%E9%97%A8%EF%BC%8C%E7%94%A8%E8%BF%87%E4%BC%9A%E7%AB%8B%E9%A9%AC%E6%8F%90%E5%8D%87%E9%A2%9C%E5%80%BC%E5%93%A6%EF%BC%8C%E4%BD%A0%E7%9F%A5%E9%81%93%E4%BA%86%E5%90%97&ari=2&dbv=0&drs=3&pcs=1680x439&pss=1680x3139&cfv=0&cpl=0&chi=1&cce=true&cec=UTF-8&tlm=1560259149&rw=439&ltu=http%3A%2F%2Fbaijiahao.baidu.com%2Fs%3Fid%3D1636038282713233707&ltr=http%3A%2F%2Fnews.baidu.com%2F&ecd=1&uc=1680x961&pis=-1x-1&sr=1680x1050&tcn=1560287950&dc=4
    	byteBuf.writeBytes(_2_ReturnLine);
    	// reset bytebuf read_size, ensure readable
    	byteBuf.resetReaderIndex();
    	
    	return request;
    }
    
}
