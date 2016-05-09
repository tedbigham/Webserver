package com.blizzard.test.metrics;

/**
 * Notifies the creator of a timer when it has stopped.
 * 
 * @author Ted Bigham
 */
public interface TimerCallback {
	public void stopped(Timer timer);
}
