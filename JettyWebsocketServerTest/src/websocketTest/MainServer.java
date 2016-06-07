package websocketTest;

import websocketTest.CassesroleWebStates;


public class MainServer {
	
	static CasseroleWebServer webserver = new CasseroleWebServer();
	static TestJSONDataSource datasource = new TestJSONDataSource();
	static CalWrangler wrangler = new CalWrangler();

	public static void main(String[] args) {
		
		CassesroleWebStates.setCalWrangler(wrangler);
		
		datasource.startDataGeneration();
		webserver.startServer();

	}

}
