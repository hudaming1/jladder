package org.jladder.ext.monitor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionMonitor extends ChannelDuplexHandler {

	private static final AtomicLong InTotal = new AtomicLong();
	private static final AtomicLong OutTotal = new AtomicLong();
	private static final Map<Long, AtomicInteger> InMonitor = new ConcurrentHashMap<>();
	private static final Map<Long, AtomicInteger> OutMonitor = new ConcurrentHashMap<>();
	private static final Timer TIMER = new Timer();
	private static final BigDecimal _1M = new BigDecimal(1000000);
	
	static {
		TIMER.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				long key = System.currentTimeMillis() / 1000;
				AtomicInteger inFlow = InMonitor.get(key);
				AtomicInteger outFlow = OutMonitor.get(key);
				log.info(String.format("in=%s/%s, out=%s/%s ", parse(inFlow == null ? 0: inFlow.get()), parse(InTotal.get()),
						parse(outFlow == null ? 0 : outFlow.get()), parse(OutTotal.get())));
			}
		}, 0L, 1000L);
		log.info("Connection Monitor Started");
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof ByteBuf) {
    		ByteBuf byteBuf = (ByteBuf) msg;
    		long key = System.currentTimeMillis() / 1000;
    		InMonitor.putIfAbsent(key, new AtomicInteger());
    		InMonitor.get(key).addAndGet(byteBuf.readableBytes());
    		InTotal.addAndGet(byteBuf.readableBytes());
    	}
    	
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof ByteBuf) {
    		ByteBuf byteBuf = (ByteBuf) msg;
    		long key = System.currentTimeMillis() / 1000;
    		OutMonitor.putIfAbsent(key, new AtomicInteger());
    		OutMonitor.get(key).addAndGet(byteBuf.readableBytes());
    		OutTotal.addAndGet(byteBuf.readableBytes());
    	}
    	
        ctx.write(msg, promise);
    }
    
    private static String parse(long bytes) {
    	if (bytes > 1000000) {
    		BigDecimal decimal = new BigDecimal(bytes);
    		return decimal.divide(_1M).setScale(2, BigDecimal.ROUND_FLOOR) + " MB";
    	} else if (bytes > 1000) {
    		return (bytes / 1000) + " KB";
    	} else {
    		return bytes + " Byte";
    	}
    }
}
