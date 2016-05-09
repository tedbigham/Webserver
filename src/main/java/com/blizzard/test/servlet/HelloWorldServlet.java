package com.blizzard.test.servlet;

import java.io.PrintWriter;

import com.blizzard.test.SimpleServlet;
import com.blizzard.test.SimpleServletRequest;
import com.blizzard.test.SimpleServletResponse;

/**
 * Sends an HTML "Hello" page to the client.
 * 
 * @author Ted Bigham
 */
public class HelloWorldServlet implements SimpleServlet {
	
	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
		PrintWriter out = new PrintWriter(response.getOutputStream(), true);
		response.setMimeType("text/html");
		out.println("<html><body>Hello!</body></html>");
	}	
}
