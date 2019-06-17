package org.hum.nettyproxy.adapter.http.simpleserver.enumtype;

public enum RequestTypeEnum {
	
	HTML("html", "text/html;"),
	HTM("htm", "text/html;"),
	;

	private String suffix;
	private String contentType;
	
	RequestTypeEnum(String suffix, String contentType) {
		this.suffix = suffix;
		this.contentType = contentType;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getContentType() {
		return contentType;
	}
	
	public static RequestTypeEnum get(String suffix) {
		if (suffix == null || suffix.isEmpty()) {
			return null;
		}
		for (RequestTypeEnum requestType : values()) {
			if (requestType.getSuffix().equalsIgnoreCase(suffix)) {
				return requestType;
			}
		}
		return null;
	}
}
