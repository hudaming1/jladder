package org.hum.nettyproxy.adapter.http.capture;

import java.util.Date;

import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.model.HttpResponse;

import lombok.Data;

@Data
public class CaptureRecord {

	private HttpRequest request;
	private HttpResponse response;
	private Date requestDate;
	private Date responseDate;
	
	public CaptureRecord() {
	}
	
	public CaptureRecord(HttpRequest request) {
		this.request = request;
	}
}
