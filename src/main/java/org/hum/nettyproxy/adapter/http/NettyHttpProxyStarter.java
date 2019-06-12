package org.hum.nettyproxy.adapter.http;

import org.hum.nettyproxy.ServerRun.Starter;
import org.hum.nettyproxy.ServerRunArg;

public class NettyHttpProxyStarter implements Starter {

	@Override
	public void start(ServerRunArg args) {
		new Thread(new NettyHttpProxy(args.getPort(), args.getWorkerCnt())).start();
	}
}
