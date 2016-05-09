package com.blizzard.test.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.blizzard.test.ServletMetadata;
import com.blizzard.test.SimpleServlet;
import com.blizzard.test.SimpleServletRequest;
import com.blizzard.test.SimpleServletResponse;
import com.blizzard.test.metrics.Timer;

/** 
 * ServletMetadata is mostly a POJO.  But there's a little logic so we test it. 
 */
public class ServletMetadataTest {
	/** Tests the metrics in ServletMetadata. */
	@Test
	public void testTimer() throws Exception {
		ServletMetadata md = new ServletMetadata(null,null,null);
		Timer t = md.getTimer();
		assertEquals(1, md.getInFlightCount());
		assertEquals(1, md.getRequestCount());
		Thread.sleep(1000);
		t.stop();
		assertEquals(0, md.getInFlightCount());
		assertEquals(1, md.getRequestCount());
		assertTrue(md.getAvgRequestTime() >= 900);
	}

	/** Make sure the constructor is putting stuff where we expect it. */
	@Test
	public void testConstructor() {
		SimpleServlet mockServlet = new SimpleServlet(){@Override
		public void doGet(SimpleServletRequest request,	SimpleServletResponse response) { }};
		ServletMetadata md = new ServletMetadata("name", "pattern", mockServlet);
		assertEquals("name", md.getName());
		assertEquals("pattern", md.getPattern());
		assertEquals(mockServlet, md.getTarget());
	}
}
