package org.hum.nettyproxy.adapter.http.capture.outter;

import org.hum.nettyproxy.adapter.http.capture.HttpCapturePrinter;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.model.HttpResponse;

public class HttpCaptureLogPrinter implements HttpCapturePrinter {
	
	private final String outStringTemplate = "==========================================\n"
			+ "%s\n"
			+ "HTTP %s\n"
			+ "%s\n"
			+ "==========================================\n";

	@Override
	public void flush(HttpRequest request, HttpResponse response) {
		String url = request.getProtocol() + "://" + request.getHeaders().get("Host") + request.getUri();
		System.out.println(String.format(outStringTemplate, url, (response == null ? 0 : response.getCode()), (response == null? null : response.toContent())));
	}
}
