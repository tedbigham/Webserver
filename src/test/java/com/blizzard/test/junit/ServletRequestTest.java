package com.blizzard.test.junit;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import com.blizzard.test.impl.SimpleServletRequestImpl;

/**
 * Test the HTTP request parsing logic.
 * 
 * @Author Ted Bigham
 */
public class ServletRequestTest  {
	@Test
	public void testParseRequestLine() throws IOException {
		Socket socket = new Socket() {
			public java.io.InputStream getInputStream() throws java.io.IOException {
				return new ByteArrayInputStream("GET /index.html?parm1=value1&parm2=value2&parm3 HTTP/1.0\n".getBytes());
			}
		};
		SimpleServletRequestImpl req = new SimpleServletRequestImpl(socket);
		assertEquals("/index.html?parm1=value1&parm2=value2&parm3", req.getUrlPath());
		assertEquals("value1", req.getUrlParameter("parm1"));
		assertEquals("value2", req.getUrlParameter("parm2"));
		assertEquals("", req.getUrlParameter("parm3"));
	}

	@Test
	public void testParseHeaders() throws IOException {
		Socket socket = new Socket() {
			public java.io.InputStream getInputStream() throws java.io.IOException {
				return new ByteArrayInputStream("GET / HTTP/1.0\nHost: myhost\nX-some-other-header: xxx\n\n".getBytes());
			}
		};
		SimpleServletRequestImpl req = new SimpleServletRequestImpl(socket);
		assertEquals("myhost", req.getHost());
		assertEquals("xxx", req.getHeader("x-some-other-header"));
	}
}
