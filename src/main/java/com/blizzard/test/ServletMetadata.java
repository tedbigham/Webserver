package com.blizzard.test;

import java.util.concurrent.atomic.AtomicLong;

import com.blizzard.test.metrics.Timer;
import com.blizzard.test.metrics.TimerCallback;

/** 
 * Holding information about a servlet.
 * 
 * @Author Ted Bigham
 */
public class ServletMetadata implements TimerCallback {

	private SimpleServlet target;
	private String pattern;
	private String name;
	private AtomicLong requestCount = new AtomicLong();
	private AtomicLong inFlightCount = new AtomicLong();
	private AtomicLong totalTime = new AtomicLong();
	private AtomicLong errorCount = new AtomicLong();
	
	public ServletMetadata(String name, String pattern, SimpleServlet target) {
		this.name = name;
		this.pattern = pattern;
		this.target = target;
	}

	public String getName() {
		return name;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public SimpleServlet getTarget() {
		return target;
	}
	
	public long getRequestCount() {
		return requestCount.get();
	}
	
	public long getInFlightCount() {
		return inFlightCount.get();
	}
	
	public double getAvgRequestTime() {
		if (requestCount.get() == 0)
			return 0;
		
		return (double)totalTime.get() / (double)requestCount.get();
	}
	
	public void addError() {
		errorCount.incrementAndGet();
	}
	
	public long getErrorCount() {
		return errorCount.get();
	}
	
	/** this assumes you are timing a request */
	public Timer getTimer() {
		requestCount.incrementAndGet();
		inFlightCount.incrementAndGet();
		return new Timer(this);
	}

	@Override
	public void stopped(Timer timer) {
		inFlightCount.decrementAndGet();
		totalTime.addAndGet(timer.getElapsedTime());
	}
	
}
