package websocketTest;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.lib.WebServer.CassesroleWebStates;

public class TestJSONDataSource {
	
	public int TestData1;
	public double TestData2;
	public boolean TestBool;
	
	public int counter;
	
	public Calibration cal1;
	public Calibration cal2;
	
	public void initDataGeneration(){
				
		CasseroleDriverView.newDial("Test Val1 RPM", 0, 200, 25, 55, 130);
		CasseroleDriverView.newDial("Test Val2 ft/s", -20, 20, 5, -3, 3);
		CasseroleDriverView.newDial("Battery Volts", 0, 15, 1, 10.5, 13.5);
		CasseroleDriverView.newWebcam("Test WebCam", "http://rax1.bsn.net/mjpg/video.mjpg");
		CasseroleDriverView.newBoolean("TestBool");
		
	}
	
	public void startDataGeneration(){
		cal1 = new Calibration("Cal1", 1.5,-5,40.5);
		cal2 = new Calibration("Cal2",87.23);
		counter = 0;
		
		Thread dataGenThread = new Thread(new Runnable() {
			@Override
			public void run(){
				while(true){
					TestData1 = TestData1 - 3 + (int)cal1.get();
					TestData2 = TestData1/2.0 + 4.0 + cal2.get();
					TestBool = !TestBool;
					
					CasseroleDriverView.setDialValue("Test Val1 RPM", cal1.get()*Math.sin(counter/30.0)+cal2.get());
					
					CassesroleWebStates.putInteger("Test Data #1", TestData1);
					CassesroleWebStates.putDouble("Test Data #2", TestData2);
					CassesroleWebStates.putBoolean("Test Boolean", TestBool);
					
					CassesroleWebStates.putString("Test String", "Very special things!");
					
					counter++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});
		
		TestData1 = 0;
		TestData2 = 0;
		TestBool = false;
		dataGenThread.setName("CasseroleTestDataGenerator");
		dataGenThread.start();
	}

}
