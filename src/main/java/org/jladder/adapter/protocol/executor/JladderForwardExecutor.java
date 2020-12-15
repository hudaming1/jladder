package org.jladder.adapter.protocol.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.jladder.adapter.protocol.listener.JladderForwardListener;
import org.jladder.adapter.protocol.message.JladderMessage;
import org.jladder.common.core.NettyProxyContext;
import org.jladder.common.core.config.JladderConfig;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * Proxy连接池只关注实现连接池的策略即可
 * @author hudaming
 */
@Slf4j
public class JladderForwardExecutor {
	
	private List<JladderCryptoForwardWorker> jladderForwardWorkerList = new ArrayList<>();
	private AtomicInteger RoundRobinRouter = new AtomicInteger(0);
	private int outsideChannelCount = 2;
	private final static NioEventLoopGroup loopGroup = new NioEventLoopGroup(1);
	private final CountDownLatch latch = new CountDownLatch(outsideChannelCount);
	
	public JladderForwardExecutor() {
		JladderConfig config = NettyProxyContext.getConfig();
		for (int i = 0 ;i < outsideChannelCount; i ++) {
			JladderCryptoForwardWorker jladderForwardWorker = new JladderCryptoForwardWorker(config.getOutsideProxyHost(), config.getOutsideProxyPort(), loopGroup);
			jladderForwardWorker.connect().onConnect(event -> {
				latch.countDown();
			});
			jladderForwardWorkerList.add(jladderForwardWorker);
		}
		try {
			latch.await();
			log.info(outsideChannelCount + " ForwardWorker inited...");
		} catch (InterruptedException e) {
			log.error("init worker failed..", e);
		}
	}

	public JladderForwardListener writeAndFlush(JladderMessage message) {
//		if (message instanceof JladderDataMessage) {
//			log.info("flushmessage=" + ((JladderDataMessage) message).getBody().toString(CharsetUtil.UTF_8));
//		}
		return select().writeAndFlush(message);
//		JladderConfig config = NettyProxyContext.getConfig();
//		CountDownLatch latch = new CountDownLatch(1);
//		JladderCryptoForwardWorker jladderCryptoForwardWorker = new JladderCryptoForwardWorker(config.getOutsideProxyHost(), config.getOutsideProxyPort(), loopGroup);
//		jladderCryptoForwardWorker.connect().onConnect(e -> {
//			latch.countDown();
//		});
//		try {
//			latch.await();
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//		return jladderCryptoForwardWorker.writeAndFlush(message);
	}
	
	protected JladderCryptoForwardWorker select() {
		return jladderForwardWorkerList.get(RoundRobinRouter.getAndIncrement() % outsideChannelCount);
	}
}
