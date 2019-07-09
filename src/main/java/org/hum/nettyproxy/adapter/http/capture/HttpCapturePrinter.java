package org.hum.nettyproxy.adapter.http.capture;

import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.model.HttpResponse;

/**
 * 仅定义抓到HTTP后如何输出
 * @author hudaming
 */
public interface HttpCapturePrinter {

	public void flush(HttpRequest request, HttpResponse response);
}
