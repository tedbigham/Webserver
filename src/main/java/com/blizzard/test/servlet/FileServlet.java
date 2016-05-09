package com.blizzard.test.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.blizzard.test.Configurable;
import com.blizzard.test.HttpException;
import com.blizzard.test.Server;
import com.blizzard.test.SimpleServlet;
import com.blizzard.test.SimpleServletRequest;
import com.blizzard.test.SimpleServletResponse;
import com.blizzard.test.util.Util;

/**
 * This serves files below the root folder tree.
 * 
 * TODO: This needs a ton of security added
 * <li>prevent pathing outside of document root with dot-dot (..)
 * <li>prevent accessing different drives using colon (:)
 * <li>I'm sure there's more
 * 
 * @author Ted Bigham
 */
public class FileServlet implements SimpleServlet, Configurable {
	private File rootFolder;
	private String indexPage;
	
	@Override
	public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
		String urlPath = request.getUrlPath();
		String path;
		if(urlPath.contains("://")) {
			try {
				URL url = new URL(urlPath);
				path = url.getPath();
			} catch (MalformedURLException e) {
				throw new HttpException(400, "Malformed Request");
			}
		}
		path = urlPath.split("\\?")[0];
		if (path.equals("/"))
			path = indexPage;
		
		File target = new File(rootFolder, path);
		FileInputStream in = null;
		try {
			in = new FileInputStream(target);
			Util.pipe(in, response.getOutputStream());
		} catch (FileNotFoundException e) {
			throw new HttpException(404, "Not Found", "File not found: " + path);
		} catch (IOException e) {
			throw new HttpException(500, "Internal Server Error", "An unexpected error occured");
		} finally {
			Util.close(in);
		}
	}

	@Override
	public void configure(String servletName, Server server) {
		String root = server.getConfig().getProperty("servlet." + servletName + ".rootFolder");
		indexPage = server.getConfig().getProperty("servlet." + servletName + ".indexPage","index.html");
		rootFolder = new File(root);
		if (!rootFolder.exists())
			throw new RuntimeException("root folder does not exist: " + rootFolder.getAbsolutePath());
	}
}
