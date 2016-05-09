package com.blizzard.test;


/**
 * Allows a servlet to know its own name, and what server it running on.
 * 
 * @Author Ted Bigham
*/
public interface Configurable {

	/** this called once by the server framework before any requests are sent */
	public void configure(String servletName, Server server);
	
}
