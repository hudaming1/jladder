package org.jladder.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private List<ByteBuf> content = new ArrayList<>();

	public String toUtfText() {
		if (!headers.containsKey("Content-Type")) {
			return "not text format";
		}
		if (headers.get("Content-Type").contains("text/html") || headers.get("Content-Type").contains("text/javascript")
				|| headers.get("Content-Type").contains("text/css")
				|| headers.get("Content-Type").contains("application/json")) {
			StringBuilder sbuilder = new StringBuilder();
			for (ByteBuf byteBuf : content) {
				sbuilder.append(byteBuf.toString(CharsetUtil.UTF_8));
			}
			return sbuilder.toString();
		} else {
			return "not text format";
		}
	}
}
