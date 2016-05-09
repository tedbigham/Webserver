package com.blizzard.test.junit;

import java.io.ByteArrayInputStream;
import java.net.Socket;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.blizzard.test.RequestHandler;
import com.blizzard.test.Server;
import com.blizzard.test.ServletMetadata;
import com.blizzard.test.SimpleServlet;
import com.blizzard.test.SimpleServletRequest;
import com.blizzard.test.SimpleServletResponse;

/**
 * Tests the main handler for web requests.
 *
 * @author Ted Bigham
 */
public class RequestHandlerTest {
	
	@Test
	public void testGoodRequest() throws Exception {
		Mockery context = new JUnit4Mockery() {{
			setImposteriser(ClassImposteriser.INSTANCE);
		}};
		final Server server = context.mock(Server.class);
		final Socket socket = context.mock(Socket.class);
		final SimpleServlet servlet = context.mock(SimpleServlet.class);
		final ServletMetadata metadata = new ServletMetadata("test","/",servlet);
		
		RequestHandler handler = new RequestHandler(socket, server);
		
		context.checking(new Expectations() {{
			oneOf(socket).getInetAddress();
			oneOf(socket).getOutputStream();
			oneOf(socket).getInputStream(); will(returnValue(new ByteArrayInputStream("GET /test HTTP/1.0".getBytes())));
			oneOf(socket).close();
		    oneOf(server).getServlet("/test"); will(returnValue(metadata));
		    oneOf(servlet).doGet(with(any(SimpleServletRequest.class)), with(any(SimpleServletResponse.class)));
		}});
		
		handler.run();
		context.assertIsSatisfied();
	}
	
	@Test
	public void testBadRequest() throws Exception {
		Mockery context = new JUnit4Mockery() {{
			setImposteriser(ClassImposteriser.INSTANCE);
		}};
		final Server server = context.mock(Server.class);
		final Socket socket = context.mock(Socket.class);
		
		RequestHandler handler = new RequestHandler(socket, server);
		
		context.checking(new Expectations() {{
			oneOf(socket).getInetAddress();
			oneOf(socket).getOutputStream();
			oneOf(socket).getInputStream(); will(returnValue(new ByteArrayInputStream("badrequest".getBytes())));
			oneOf(socket).close();
		    oneOf(server).addError();
		    oneOf(server).getPort(); // we print the port in the error message
		}});
		
		handler.run();
		context.assertIsSatisfied();
	}
}
