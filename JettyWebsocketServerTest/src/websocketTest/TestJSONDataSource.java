package websocketTest;

import org.usfirst.frc.team1736.lib.Calibration.Calibration;
import org.usfirst.frc.team1736.lib.DataServer.CasseroleDataServer;
import org.usfirst.frc.team1736.lib.DataServer.Signal;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleDriverView;
import org.usfirst.frc.team1736.lib.WebServer.CasseroleWebPlots;
import org.usfirst.frc.team1736.lib.WebServer.CassesroleWebStates;

public class TestJSONDataSource {
	
	public int TestData1;
	public double TestData2;
	public double TestData3;
	public boolean TestBool;
	
	public double counter;
	
	public Calibration cal1;
	public Calibration cal2;
	
	Signal testSig1;
	Signal counterSig;
	Signal memoryUsedSig;
	Signal storedSamplesSig;
	
	public void initDataGeneration(){
				
		CasseroleDriverView.newDial("Test Val1 RPM", 0, 200, 25, 55, 130);
		CasseroleDriverView.newDial("Test Val2 ft/s", -20, 20, 5, -3, 3);
		CasseroleDriverView.newDial("Battery Volts", 0, 15, 1, 10.5, 13.5);
		CasseroleDriverView.newWebcam("Test WebCam", "http://plazacam.studentaffairs.duke.edu/mjpg/video.mjpg", 50.0, 25.0, 90.0);
		CasseroleDriverView.newWebcam("Test WebCam2", "http://86.41.192.75:1024/mjpg/video.mjpg", 25.0, 75.0, 90.0);
		CasseroleDriverView.newBoolean("Test Bool Display 1", "red");
		CasseroleDriverView.newBoolean("Test Bool Display 2", "green");
		CasseroleDriverView.newBoolean("Test Bool Display 3", "yellow");
		CasseroleDriverView.newStringBox("Test String");
		CasseroleDriverView.newStringBox("Test String2");
		CasseroleDriverView.newStringBox("Test String3");
		CasseroleDriverView.newAutoSelector("Auto 1",  new String[]{"Test 1","Another Wonderful Test", "Test 35"});
		CasseroleDriverView.newAutoSelector("Auto Two",  new String[]{"One Fish","Two Fish", "Red Fish", "Blue Fish"});
		
		
		CasseroleWebPlots.addNewSignal("Test Val1", "RPM");
		CasseroleWebPlots.addNewSignal("Test Val2", "ft/s");
		CasseroleWebPlots.addNewSignal("Battery Volts", "V");
		CasseroleWebPlots.addNewSignal("Battery Current", "A");
		CasseroleWebPlots.addNewSignal("DT Left Motor Speed", "RPM");
		CasseroleWebPlots.addNewSignal("DT Right Motor Speed", "RPM");
		CasseroleWebPlots.addNewSignal("Shooter Motor Speed", "RPM");
		
		testSig1 = new Signal("Test Value 1", "km/h");
		counterSig = new Signal("Loop Counter", "Loops");
		memoryUsedSig = new Signal("JVM Memory", "Kb");
		storedSamplesSig = new Signal("Data Server Stored Sample count", "");
		
		
	}
	
	public void startDataGeneration(){
		cal1 = new Calibration("Cal1", 10,-5,100);
		cal2 = new Calibration("Cal2",15.0);
		counter = 0;
		
		Thread dataGenThread = new Thread(new Runnable() {
			@Override
			public void run(){
				while(true){
					TestData1 = TestData1 - 3 + (int)cal1.get();
					TestData2 = TestData1/2.0 + 4.0 + cal2.get();
					TestData3 = cal1.get()*Math.sin(counter/cal2.get())+50;
					TestBool = TestData3 > 87.0;
					
					CasseroleWebPlots.addSample("Test Val1", counter*0.02, TestData3);
					CasseroleWebPlots.addSample("Test Val2", counter*0.02, TestData3 * TestData3);
					CasseroleWebPlots.addSample("Battery Volts", counter*0.02, (counter/5.0) % 12);
					CasseroleWebPlots.addSample("Battery Current", counter*0.02,TestData3*TestData1 % 3000);
					CasseroleWebPlots.addSample("DT Left Motor Speed", counter*0.02,(counter/5.0) % 12 * TestData3 * 0.1);
					CasseroleWebPlots.addSample("DT Right Motor Speed", counter*0.02,Math.floor(TestData3/5)*5);
					CasseroleWebPlots.addSample("Shooter Motor Speed", counter*0.02,Math.random()+TestData3);
					
					
					CassesroleWebStates.putInteger("Test Data #1", TestData1);
					CassesroleWebStates.putDouble("Test Data #2", TestData2);
					CassesroleWebStates.putBoolean("Battery Volts", TestBool);
					
					
					
					
					CasseroleDriverView.setDialValue("Test Val1 RPM", TestData3);
					CasseroleDriverView.setDialValue("Test Val2 ft/s", 5.0);
					CasseroleDriverView.setDialValue("Battery Volts", (counter/5.0) % 12);
					CasseroleDriverView.setStringBox("Test String2", "Test value " + Double.toString(counter));
					CasseroleDriverView.setStringBox("Test String3", CasseroleDriverView.getAutoSelectorVal("Auto 1"));
					CasseroleDriverView.setBoolean("Test Bool Display 1", TestData3 > 45.0);
					CasseroleDriverView.setBoolean("Test Bool Display 2", TestData3 > 50.0);
					CasseroleDriverView.setBoolean("Test Bool Display 3", TestData3 > 55.0);
					CasseroleDriverView.setStringBox("Test String", CasseroleDriverView.getAutoSelectorVal("Auto Two"));
					
					CassesroleWebStates.putString("Test String", CasseroleDriverView.getAutoSelectorVal("Auto Two"));
					
					double sampleTime = System.currentTimeMillis();
					counterSig.addSample(sampleTime, counter);
					testSig1.addSample(sampleTime, TestData3);
					
					counter++;

					if(((int)counter)%10==0){
						Runtime rt = Runtime.getRuntime();
						long total = rt.totalMemory();
						long free = rt.freeMemory();
						double used_kb = ((double)total - (double)free)/1024.0;
						memoryUsedSig.addSample(sampleTime, used_kb);
						storedSamplesSig.addSample(sampleTime, CasseroleDataServer.getInstance().getTotalStoredSamples());
					}
					
					try {
						Thread.sleep(20);
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
