package com.blizzard.test.metrics;

/**
 * For measuring a single instance of elapsed time.
 * 
 * @author Ted Bigham
 *
 */
public class Timer {
	private TimerCallback owner;
	private long startTime;
	private long endTime;
	
	public Timer(TimerCallback owner) {
		this.owner = owner;
		startTime = System.currentTimeMillis();
	}
	
	public void stop() {
		endTime = System.currentTimeMillis();
		owner.stopped(this);
	}
	
	public long getElapsedTime() {
		return endTime - startTime;
	}
}
