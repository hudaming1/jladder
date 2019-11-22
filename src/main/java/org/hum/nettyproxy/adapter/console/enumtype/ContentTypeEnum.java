package org.hum.nettyproxy.adapter.console.enumtype;

/**
 * ContentType对照表
 * 	<pre>
 *    全量ContentType可参照：http://tool.oschina.net/commons/
 * 	</pre>
 * @author huming
 */
public enum ContentTypeEnum {
	
	HTML("html", "text/html;"),
	HTM("htm", "text/html;"),
	JPG("jpg", "application/x-jpg;"),
	PNG("png", "image/png;"),
	JPEG("jpeg", "image/jpeg;"),
	CSS("css", "text/css;"),
	JS("js", "application/x-javascript;"),
	;

	private String suffix;
	private String contentType;
	
	ContentTypeEnum(String suffix, String contentType) {
		this.suffix = suffix;
		this.contentType = contentType;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getContentType() {
		return contentType;
	}
	
	public static ContentTypeEnum get(String suffix) {
		if (suffix == null || suffix.isEmpty()) {
			return null;
		}
		for (ContentTypeEnum requestType : values()) {
			if (requestType.getSuffix().equalsIgnoreCase(suffix)) {
				return requestType;
			}
		}
		return null;
	}
}
