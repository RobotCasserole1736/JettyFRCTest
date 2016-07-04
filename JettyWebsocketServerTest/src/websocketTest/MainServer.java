package websocketTest;

import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebServer;
import org.usfirst.frc.team1736.lib.WebServer.CassesroleDriverView;
import org.usfirst.frc.team1736.lib.WebServer.CassesroleWebStates;

public class MainServer {
	
	static CasseroleWebServer webserver = new CasseroleWebServer();
	static TestJSONDataSource datasource = new TestJSONDataSource();
	static CalWrangler wrangler = new CalWrangler();

	public static void main(String[] args) {
		
		CassesroleDriverView.newDial("Test Val1 (RPM)", 0, 200, 25);
		CassesroleDriverView.newDial("Test Val2 (ft/s)", -20, 20, 5);
		CassesroleDriverView.newDial("Battery Volts", 0, 15, 1);
		CassesroleDriverView.newWebcam("http://rax1.bsn.net/mjpg/video.mjpg");
		
		datasource.startDataGeneration();
		webserver.startServer();

	}

}
