package websocketTest;

import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebServer;
import org.usfirst.frc.team1736.lib.WebServer.CassesroleWebStates;

public class MainServer {
	
	static CasseroleWebServer webserver = new CasseroleWebServer();
	static TestJSONDataSource datasource = new TestJSONDataSource();
	static CalWrangler wrangler = new CalWrangler();

	public static void main(String[] args) {
		
		CasseroleDriverView.newDial("Test Val1 RPM", 0, 200, 25, 55, 130);
		CasseroleDriverView.newDial("Test Val2 ft/s", -20, 20, 5, -3, 3);
		CasseroleDriverView.newDial("Battery Volts", 0, 15, 1, 10.5, 13.5);
		CasseroleDriverView.newWebcam("Test WebCam", "http://rax1.bsn.net/mjpg/video.mjpg");
		CasseroleDriverView.newBoolean("TestBool");

		datasource.startDataGeneration();
		webserver.startServer();

	}

}
