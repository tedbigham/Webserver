package com.blizzard.test.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.blizzard.test.HttpException;
import com.blizzard.test.SimpleServletRequest;

/**
 * This parses the request data and makes it available as SimpleServletRequest.
 * 
 * @Author Ted Bigham
 */
public class SimpleServletRequestImpl implements SimpleServletRequest {
	private Map<String,String> headers = new HashMap<>();
	private Map<String,String> urlParms = new HashMap<>();
	private String urlPath;
	private InetAddress clientAddress;
	private BufferedReader in;
	
	/** The input stream is parsed in the constructor. */
	public SimpleServletRequestImpl(Socket socket) throws IOException {
		clientAddress = socket.getInetAddress();
		InputStream in = socket.getInputStream();
		this.in = new BufferedReader(new InputStreamReader(in));
		String requestLine = this.in.readLine();
		parseHeaders();
		parseRequestLine(requestLine);
		parseParams();
	}
	
	/** A valid request line looks something like "GET /index.html HTML/1.1" */
	private void parseRequestLine(String requestLine) throws IOException {
		if (requestLine == null)
			throw new HttpException(400, "Malformed Request", "The request had no content");

		String[] parts = requestLine.split(" ");
		if (parts.length != 3)
			throw new HttpException(400, "Malformed Request", "The request line didn't have 3 parts");

		if (!parts[0].equals("GET"))
			throw new HttpException(400, "Method Not Supported", "Method not supported '" + parts[0] + "'");
		
		urlPath = parts[1];
	}
	
	/** 
	 * Header format is multiple lines, each containing "Name: Value"
	 * Names are converted to lower case.  If there are multiple headers with 
	 * the same name, the latest one is kept.
	 */ 
	private void parseHeaders() throws IOException {
		String line = in.readLine();
		while(line != null && line.trim().length() > 0) {
			String[] parts = line.split(":");
			if (parts.length == 2) {
				setHeader(parts[0].trim(), parts[1].trim());
			} else {
				setHeader(parts[0].trim(), "");
			}
			line = in.readLine();
		}
	}

	/** parameter with no value are stored as empty strings */
	private void parseParams() throws IOException {
		int question = getUrlPath().indexOf("?");
		if (question == -1)
			return;
		
		String parms = getUrlPath().substring(question+1);
		for (String parm : parms.split("&")) {
			int equal = parm.indexOf("=");
			if (equal == -1) {
				urlParms.put(parm,"");
			} else {
				String name = parm.substring(0, equal);
				String value = parm.substring(equal+1);
				urlParms.put(name, value);
			}
		}
	}
	
	@Override
	public String getHost() {
		return getHeader("Host");
	}

	@Override
	public String getUrlPath() {
		return urlPath;
	}

	@Override
	public String getUrlParameter(String name) {
		return urlParms.get(name);
	}

	public void setHeader(String name, String value) {
		headers.put(name.toLowerCase(), value);
	}	

	@Override
	public String getHeader(String name) {
		return headers.get(name.toLowerCase());
	}	

	@Override
	public InetAddress getClientAddress() {
		return clientAddress;
	}

	public Map<String, String> getUrlParameters() {
		return urlParms;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

}
