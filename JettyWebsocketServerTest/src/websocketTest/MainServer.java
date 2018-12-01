package websocketTest;

import org.usfirst.frc.team1736.lib.Calibration.CalWrangler;
import org.usfirst.frc.team1736.lib.DataServer.CasseroleDataServer;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebServer;

public class MainServer {
    
    static CasseroleWebServer webserver = new CasseroleWebServer();
    static TestJSONDataSource datasource = new TestJSONDataSource();
    static CalWrangler wrangler = new CalWrangler();

    public static void main(String[] args) {

        datasource.initDataGeneration();
        datasource.startDataGeneration();

        webserver.setResourceBase("resources/");
        webserver.startServer();
        
        CasseroleDataServer.getInstance().startServer();

    }

}
