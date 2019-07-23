package org.hum.nettyproxy.adapter.http.capture.outter;

import org.hum.nettyproxy.adapter.http.capture.HttpCapturePrinter;
import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.handler.codec.http.HttpResponse;

public class HttpCaptureLogPrinter implements HttpCapturePrinter {

	@Override
	public void flush(HttpRequest request, HttpResponse response) {
		String url = request.getProtocol() + "://" + request.getHeaders().get("Host") + request.getUri();
		System.out.println("=========>" + url + "=============>" + response);
	}
}
