package com.blizzard.test.servlet;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import com.blizzard.test.SimpleServlet;
import com.blizzard.test.SimpleServletRequest;
import com.blizzard.test.SimpleServletResponse;
import com.blizzard.test.impl.SimpleServletRequestImpl;

/**
 * Responds with a page summarizing the browser request.
 * 
 * @author Ted Bigham
 */
public class EchoServlet implements SimpleServlet {
	
	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
		PrintWriter out = new PrintWriter(response.getOutputStream(), true);
		out.println("<html><head>");
		out.println("<p>URL: " + request.getUrlPath());
		out.println("<p>Host:" + request.getHost());
		out.println("<p>Client Address: " + request.getClientAddress());

		// cast the request for specific functionality without changing the interface 
		SimpleServletRequestImpl myRequest = (SimpleServletRequestImpl)request; 

		out.println("<h3>Request Parameters</h3>");
		dumpMap(out, myRequest.getUrlParameters());

		out.println("<h3>Headers</h3>");
		dumpMap(out, myRequest.getHeaders());
	}

	/** send a line item list of all the entries from the map. */
	private void dumpMap(PrintWriter out, Map<String,String> map) {
		for(Entry<String,String> entry : map.entrySet()) {
			out.println("<li>" + entry.getKey() + ":" + entry.getValue());
		}
	}
}
 
