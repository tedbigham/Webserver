package com.blizzard.test.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.blizzard.test.Server;

/** 
 * <p>This is somewhat of an integration test.  A real version of the server is
 * started and hit with real requests.</p>  
 * 
 * <p>This uses the configuration in conf/webserver.properties.  If that file 
 * changes, these tests will likely have to change too.</p>
 */
public class ServerTest {
	private Server server;
	
	@Before
	public void setup() {
		server = new Server();
		new Thread(server).start();
	}

	@After
	public void teardown() {
		if (server != null)
			server.stop();
	}
	
	/** make sure the hello world servlet is working */
	@Test
	public void testHelloWorld() throws Exception {
		URL url = new URL("http://localhost:8080/hello");
		InputStream in = url.openStream();
		byte[] buffer = new byte[100];
		int size = in.read(buffer);
		in.close();
		assertEquals("<html><body>Hello!</body></html>", new String(buffer, 0, size).trim());
		assertEquals(0, server.getErrorCount());
	}
	
	/** make sure a bad request doesn't crash the server */
	@Test(timeout=5000)
	public void testBadRequestLine() throws Exception {
		Socket s = new Socket("localhost", 8080);
		InputStream in = s.getInputStream();
		OutputStream out = s.getOutputStream();
		out.write("blah\n\n".getBytes());
		out.flush();
		byte[] buffer = new byte[100];
		int size = in.read(buffer);
		String response = new String(buffer, 0, size);
		assertEquals("HTTP/1.1 400 Malformed Request", response);
		assertTrue(server.isAlive());
		s.close();
	}
}
