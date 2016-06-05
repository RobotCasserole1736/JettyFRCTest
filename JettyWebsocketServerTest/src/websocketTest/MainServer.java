package websocketTest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import  websocketTest.CasseroleDataStreamerServlet;

import websocketTest.CasseroleBasicServlet;
import websocketTest.CasserolePingServlet;



public class MainServer {
	

	static Server server;

	public static void main(String[] args) {
		startServer();

	}

	
	
	public static void startServer(){
		
		//New server will be on the robot's address plus port 8080 (http://127.0.0.1:8080)
		server = new Server(8080);
		
		//Set up classes which will handle web requests
		//For all HTTP requests (which is the primary thing we have to service), the server has a single handler.
		//This handler has many "servlets" associated with it. These are collected by the ContextHandler (context).
		//Each servlet registered with the context will handle one subpage from the overall webpage.
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		
		ServletHolder basicHolder = new ServletHolder("basic", new CasseroleBasicServlet());
		context.addServlet(basicHolder, "/basic");
		
		ServletHolder pingHolder = new ServletHolder("ping", new CasserolePingServlet());
		context.addServlet(pingHolder, "/ping");
		
		CasseroleWebpageFromFileServlet dataWebpage = new CasseroleWebpageFromFileServlet();
		dataWebpage.setFile("./testData.html");
		ServletHolder dataWebpageHolder = new ServletHolder("data", dataWebpage);
		context.addServlet(dataWebpageHolder, "/data");
		
		ServletHolder datastreamHolder = new ServletHolder("datastream", new CasseroleDataStreamerServlet());
		context.addServlet(datastreamHolder, "/datastream");
		
		// Kick off server in brand new thread.
		// Thanks to Team 254 for an example of how to do this!
		Thread serverThread = new Thread(new Runnable() {
			@Override
			public void run(){
				try {
					server.start();
					server.join();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		serverThread.setName("CasseroleWebServerThread");
		serverThread.setPriority(Thread.MIN_PRIORITY);
		serverThread.start();

	}

}
