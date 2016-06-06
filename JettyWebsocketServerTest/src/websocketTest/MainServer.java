package websocketTest;




public class MainServer {
	
	static CasseroleWebServer webserver = new CasseroleWebServer();
	static TestJSONDataSource datasource = new TestJSONDataSource();

	public static void main(String[] args) {
		
		
		datasource.startDataGeneration();
		webserver.startServer();

	}

}
