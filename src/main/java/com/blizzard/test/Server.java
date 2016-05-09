package com.blizzard.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * The core server.  Use main() or run() to start the server.
 * 
 * @Author Ted Bigham
 */
public class Server implements Runnable {
	private static final File CONFIG_FILE = new File("conf/webserver.properties");
	private ServerSocket listener;
	private Properties config = new Properties();
	private int port;	
	private List<ServletMetadata> servlets = new ArrayList<>();
	private boolean alive = true;
	private AtomicLong errorCount = new AtomicLong();
	private int maxThreads = 20;
	private ThreadPoolExecutor threadPool;
	
	public Server() {	}
	
	public static void main(String ... args) {
		new Server().run();
	}
	
	public void run() {
		try {
			loadConfiguration();
			initThreadPool();
			mainLoop();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void mainLoop() throws IOException {
		listener = new ServerSocket(port);
		while(alive) {
			try {
				Socket socket = listener.accept();
				threadPool.execute(new RequestHandler(socket, this));
			}catch(Exception e) {
				if (alive) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("server quit");
	}

	/** the configuration is in conf/webserver.properties */
	private void loadConfiguration() {
		if (CONFIG_FILE.exists())
			configureFromFile();
		else
			configureFromDefaults();
		
		init();
		System.out.println("port: " + port);
		System.out.println("servlet mappings...");
		for (ServletMetadata md : servlets) {
			System.out.println(md.getPattern() + " -> " + md.getName() + ":" + md.getTarget().getClass());
		}
	}

	private void initThreadPool() {
		threadPool = new ThreadPoolExecutor(maxThreads, maxThreads, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		threadPool.allowCoreThreadTimeOut(true);
	}

	private void configureFromFile() {
		System.out.println("loading configuration from " + CONFIG_FILE.getAbsolutePath());
		try {
			FileInputStream in = new FileInputStream(CONFIG_FILE);
			config.load(in);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
		
	private void init() {
		port = Integer.parseInt(config.getProperty("port"));
		
		// get a CSV list of servlet names
		String servletList = config.getProperty("servlets");
		
		// load each servlet
		for (String name : servletList.split(",")) {
			loadServlet(name);
		}
		// order by length of pattern
		sortServlets();
	}

	/** Given a servlet name, load its settings from the configuration and initialize the servlet */ 
	private void loadServlet(String name) {
		String className = getConfigProperty("servlet." + name + ".class");
		String pattern = getConfigProperty("servlet." + name + ".pattern");
		try {
			Class<?> c = Class.forName(className);
			Object target = c.newInstance();
			if (!(target instanceof SimpleServlet)) {
				throw new RuntimeException("Failed loading servlet '" + name + "' " + c  + " does not implement " + SimpleServlet.class.getName());
			}
			
			if (target instanceof Configurable) {
				((Configurable)target).configure(name, this);
			}
			
			servlets.add(new ServletMetadata(name, pattern, (SimpleServlet)target));
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * We match patterns with preference to the longer patterns.  Put those 
	 * first so we can stop at the first match.
	 */
	private void sortServlets() {
		Collections.sort(servlets, new Comparator<ServletMetadata>() {
			@Override
			public int compare(ServletMetadata o1, ServletMetadata o2) {
				return o2.getPattern().length() - o1.getPattern().length();
			}
		});
	}

	// throws an exception if the property is missing */
	private String getConfigProperty(String name) {
		String value = config.getProperty(name);
		if (value == null)
			throw new RuntimeException("missing configuration parameter: '" + name + "'");
		
		return value;
	}
	
	/** the default settings serves files from the current folder */
	private void configureFromDefaults() {
		System.err.println("failed to load configuration from " + CONFIG_FILE.getAbsolutePath() + " using defaults instead");
		config.setProperty("port", "8080");
		config.setProperty("host", "localhost");
		config.setProperty("servlets", "default");
		config.setProperty("servlet.default.class","com.blizzard.servlet.FileServlet");
		config.setProperty("servlet.default.pattern","/*");
		config.setProperty("servlet.default.rootFolder",".");
	}

	/** 
	 * Returns the serlvet with a pattern matching the starting of 'path'.
	 * If there are multiple matches, the longest match one is returned.
	 */
	public ServletMetadata getServlet(String path) {
		for (ServletMetadata md : servlets) {
			if (path.startsWith(md.getPattern())) {
				return md;
			}
		}
		throw new HttpException(404, "Not Found", "File not found: " + path);
	}
	
	public int getPort() {
		return port;
	}
	
	public Properties getConfig() {
		return config;
	}
	
	public List<ServletMetadata> getServlets() {
		return servlets;
	}
	
	public void addError() {
		errorCount.incrementAndGet(); 
	}
	
	public long getErrorCount() {
		return errorCount.get();
	}

	public boolean isAlive() {
		return alive;
	}
	
	public void stop() {
		alive = false;
		threadPool.shutdown();
		try {
			listener.close();
			threadPool.awaitTermination(5, TimeUnit.SECONDS);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
