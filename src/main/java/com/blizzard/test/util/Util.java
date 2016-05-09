package com.blizzard.test.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Some static helper functions.
 */
public class Util {
	
	/** Only use static methods */
	private Util() {}
	
	/** Reads from 'in' and writes to 'out' using a 1K buffer. */
	public static void pipe(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int size = in.read(buffer);
		while(size != -1) {
			out.write(buffer, 0, size);
			size = in.read(buffer);
		}
	}
	
	/** 
	 * There's not a lot of options when we fail to close something.  This
	 * will dump the exception and return false on failure, which cleans up
	 * the calling code.
	 *  
	 * @return true if there were no errors
	 */
	public static boolean close(Closeable c) {
		if (c == null)
			return true;
		
		try {
			c.close();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
