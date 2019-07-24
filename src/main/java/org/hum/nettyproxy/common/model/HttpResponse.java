package org.hum.nettyproxy.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import lombok.Data;

@Data
public class HttpResponse implements Serializable {

	private static final long serialVersionUID = 5991693811028470848L;
	
	private String line;
	private int code;
	private Map<String, String> headers = new HashMap<String, String>();
	private ByteBuf content;

	public String toContent() {
		if (content == null) {
			return "";
		}
		return content.toString(CharsetUtil.UTF_8);
	}
}
