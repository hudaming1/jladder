package org.hum.jladder.adapter.http.capture;

import org.hum.jladder.common.model.HttpRequest;
import org.hum.jladder.common.model.HttpResponse;

/**
 * 仅定义抓到HTTP后如何输出
 * @author hudaming
 */
public interface HttpCapturePrinter {

	public void flush(HttpRequest request, HttpResponse response);
}
