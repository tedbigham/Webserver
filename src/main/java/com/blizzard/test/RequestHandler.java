package com.blizzard.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.blizzard.test.impl.SimpleServletRequestImpl;
import com.blizzard.test.impl.SimpleServletResponseImpl;
import com.blizzard.test.metrics.Timer;
import com.blizzard.test.util.Util;

/**
 * This processes a single web request.
 * <li>Parses the HTTP request.
 * <li>Creates a SimpleServletRequest and SimpleServletResponse.
 * <li>Invokes the servlet with the longest pattern matching the request
 * 
 * @Author Ted Bigham
 */
public class RequestHandler implements Runnable {
	private Socket socket;
	private Server server;
	private SimpleServletRequestImpl request;
	private SimpleServletResponseImpl response;
	
	public RequestHandler(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
	}

	/** This is the main entry point called from the executor service */
	@Override
	public void run() {
		Timer timer = null; 
		ServletMetadata metadata = null;
		try {
			response = new SimpleServletResponseImpl(socket);
			request = new SimpleServletRequestImpl(socket);
			System.out.println("request: " + request.getUrlPath());
			metadata = server.getServlet(request.getUrlPath());
			timer = metadata.getTimer(); 
			metadata.getTarget().doGet(request, response);
		} catch(Exception e) {
			if (metadata != null)
				metadata.addError();
			sendError(e);
		} finally {
			response.setHeader("connection", "close");
			response.commit();
			if (timer != null)
				timer.stop();
			Util.close(socket);
		}
	}

	/** sends a formatted error message to the browser */
	private void sendError(Exception e) {
		if (e instanceof HttpException) {
			HttpException he = (HttpException)e;
			// don't dump the stack for client errors
			if(he.getStatusCode() % 100 != 4)
				e.printStackTrace();
			sendError(he.getStatusCode(), he.getShortMessage(), he.getLongMessage());
		} else {
			e.printStackTrace();
			sendError(500, "Internal Server Error", "An unexpected error occured");
		}
	}
	
	/** sends a formatted error message to the browser */
	private void sendError(int statusCode, String shortMessage, String longMessage) {
		server.addError();
		
		if (longMessage == null)
			longMessage = "An error occured processing your request";
		
		ServletOutputStream out = (ServletOutputStream)response.getOutputStream();
		out.setStatus(statusCode, shortMessage);
		try {
			String body =
				"<title>" + statusCode + " " + shortMessage + 
				"</title></head><body><h1>" + shortMessage + 
				"</h1>" + longMessage + 
				"<hr><address>SimpleServer 1.0 at Host: " + InetAddress.getLocalHost() + 
				" Port: " + server.getPort() + "</address></body></html>";
		
			out.write(body.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
