package websocketTest;

import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebServer;
import org.usfirst.frc.team1736.lib.WebServer.CassesroleWebStates;

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
