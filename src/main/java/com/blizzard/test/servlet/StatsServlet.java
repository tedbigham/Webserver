package com.blizzard.test.servlet;

import java.io.PrintWriter;

import com.blizzard.test.Configurable;
import com.blizzard.test.Server;
import com.blizzard.test.ServletMetadata;
import com.blizzard.test.SimpleServlet;
import com.blizzard.test.SimpleServletRequest;
import com.blizzard.test.SimpleServletResponse;

/**
 * Renders the current performance statistics for the web server.
 * 
 * @author Ted Bigham
 */
public class StatsServlet implements SimpleServlet, Configurable {
	private Server server;
	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
		PrintWriter out = new PrintWriter(response.getOutputStream(), true);
		out.println("<html><head><style>body{color:#aaa;background-image:url(/bg-top.jpg)}</style><body><h2>Server Statistics</h2>");
		out.println("<table border=1><tr><th>servlet<th>path<th>class<th>requests<th>in-flight requests<th>avg response time(ms)<th>errors</tr>");
		for(ServletMetadata md : server.getServlets()) {
			out.println("<tr><td>" + md.getName());
			out.println("<td>" + md.getPattern());
			out.println("<td>" + md.getTarget().getClass().getName());
			out.println("<td>" + md.getRequestCount());
			out.println("<td>" + md.getInFlightCount());
				out.println("<td>");
			out.format("%.3f%n", md.getAvgRequestTime());
			out.println("<td>" + md.getErrorCount());
		}
		out.println("</tr></table></body></html>");
	}

	@Override
	public void configure(String servletName, Server server) {
		this.server = server;
	}
	
	
}
 
