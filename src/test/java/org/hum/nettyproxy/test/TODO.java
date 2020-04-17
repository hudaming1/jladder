package org.hum.nettyproxy.test;

public class TODO {

	/**
	 * <pre>
	 *   任务1：使用Socket作为SSL客户端与baidu进行一次完整通信，获得最终响应报文
	 *      {@link org.hum.nettyproxy.test.https_client.Test}
	 * </pre>
	 * <pre>
	 *   任务2：使用Socket作为SSL服务端，浏览器作为客户端，完成HelloWorld测试 
	 * </pre>
	 * <pre>
	 *     4月16日进展：目前通过wireshark抓包发现，在ssl握手环节，目前是Client无法处理服务端的证书，返回了SSL_ALERT(21)，而Netty没有对错误进行判断，直接将其当做证书来解析
	 *     进而也给出了错误提示，miss_tag错误
	 * </pre>
	 */
	TODO
}
