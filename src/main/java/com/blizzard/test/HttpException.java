package com.blizzard.test;


/** 
 * Holds an http status code and error message.
 * 
 * @Author Ted Bigham
 */
@SuppressWarnings("serial")
public class HttpException extends RuntimeException {
	private int statusCode;
	private String shortMessage;

	public HttpException(int statusCode, String message) {
		this(statusCode, message, message);
	}
	
	public HttpException(int statusCode, String shortMessage, String longMessage) {
		super(longMessage);
		this.statusCode = statusCode;
		this.shortMessage = shortMessage;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getShortMessage() {
		return shortMessage;
	}

	public String getLongMessage() {
		return getMessage();
	}
}
