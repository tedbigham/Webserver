package com.blizzard.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

/** 
 * This buffers an output stream for an HTTP response.  Not data is sent until
 * close() is called.  This allows the response status line, headers, and body
 * to be changed while a servlet is running.  To clear the response body, call
 * reset().  Only the body is reset.  The headers and status code are not affected,
 * 
 * TODO: Since this buffers the entire response, the number of concurrent 
 * requests will be limited by available memory and the size of the files we are
 * serving.  A more sophisticated stream would probably start flushing data once
 * some limit is reached. 
 * 
 * TODO This class needs some unit tests.
 *  
 * @author Ted Bigham
 */
public class ServletOutputStream extends ByteArrayOutputStream {
	private static final byte[] LINE_FEED = "\n".getBytes();
	private OutputStream out;
	private Map<String,String> headers;
	private String protocol = "HTTP/1.1";
	private int statusCode = 200;
	private String statusMessage = "OK";
	
	public ServletOutputStream(OutputStream out, Map<String,String> headers) {
		super(1024);
		this.out = out;
		this.headers = headers;
	}
	
	/** send the status line and headers in http format directly to the client */
	private void writeHeaders() throws IOException {
		println(protocol + " " + statusCode + " " + statusMessage);
		for (Entry<String,String> entry : headers.entrySet()) {
			println(entry.getKey() + ": " + entry.getValue());
		}
		println("Content-Length: " + size());
		println();
	}

	/** writes a line directly to the target stream */
	private void println(Object o) {
		try {
			out.write(o.toString().getBytes());
			println();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/** writes an empty line directly to the target stream */
	private void println() throws IOException {
		out.write(LINE_FEED);
	}
	
	public void setStatus(int statusCode, String statusMessage) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}
	
	/** Clears the response body. Status code and headers are unaffected */   
	public void reset() {
		super.reset();
	}
	
	/** Sends the status line, headers, and buffered content to the client */   
	public void close() throws IOException {
		writeHeaders();
		out.write(toByteArray());
		println();
		out.flush();
		out.close();
		super.close();
	}
}
