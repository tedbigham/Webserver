package com.blizzard.test.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.blizzard.test.ServletOutputStream;
import com.blizzard.test.SimpleServletResponse;

/**
 * Holds the response headers and buffered output stream and makes them 
 * available as a SimpleServletResponse.
 * 
 * When finished writing data to the client, you can either call commit() on
 * this instance, or close() on the output stream().  The only difference is
 * if there is an exception, commit() will dump it, while close() will throw it
 * instead.
 * 
 * @author Ted Bigham
 */
public class SimpleServletResponseImpl implements SimpleServletResponse {
	private ServletOutputStream out;
	private Map<String,String> headers = new HashMap<>();
	
	public SimpleServletResponseImpl(Socket socket) throws IOException {
		this.out = new ServletOutputStream(socket.getOutputStream(), headers);
	}
	
	@Override
	public void setStatusCode(int httpStatusCode) {
		out.setStatus(httpStatusCode, "");
	}

	@Override
	public void setHeader(String name, String value) {
		headers.put(name.toLowerCase(), value);
	}

	@Override
	public void setMimeType(String mimeType) {
		setHeader("Content-Type", mimeType);
	}

	@Override
	public OutputStream getOutputStream() {
		return out;
	}

	public void commit() {
		try {
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
