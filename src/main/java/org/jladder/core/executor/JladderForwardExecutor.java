package org.jladder.core.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jladder.config.JladderContext;
import org.jladder.config.JladderFullConfig;
import org.jladder.core.listener.JladderForwardListener;
import org.jladder.core.message.JladderMessage;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * Proxy连接池只关注实现连接池的策略即可
 * @author hudaming
 */
@Slf4j
public class JladderForwardExecutor {
	
	private List<JladderCryptoForwardWorker> jladderForwardWorkerList = new ArrayList<>();
	private int outsideChannelCount = 16;
	private final static NioEventLoopGroup loopGroup = new NioEventLoopGroup(1);
	private final CountDownLatch latch = new CountDownLatch(outsideChannelCount);
	
	public JladderForwardExecutor() {
		JladderFullConfig config = JladderContext.getConfig();
		for (int i = 0 ;i < outsideChannelCount; i ++) {
			JladderCryptoForwardWorker jladderForwardWorker = new JladderCryptoForwardWorker(config.getOutsideProxyHost(), config.getOutsideProxyPort(), loopGroup);
			// init connection
			jladderForwardWorker.connect().onConnect(event -> {
				latch.countDown();
			});
			jladderForwardWorkerList.add(jladderForwardWorker);
		}
		try {
			latch.await();
			log.debug(outsideChannelCount + " ForwardWorker inited...");
		} catch (Exception e) {
			log.error("init worker failed..", e);
		}
	}

	public JladderForwardListener writeAndFlush(JladderMessage message) {
		return select(message.getClientIden()).writeAndFlush(message);
	}
	
	public void clearClientIden(String iden) {
		select(iden).removeClientIden(iden);
	}
	
	protected JladderCryptoForwardWorker select(String clientIden) {
		return jladderForwardWorkerList.get(Math.abs(clientIden.hashCode()) % outsideChannelCount);
	}
}
