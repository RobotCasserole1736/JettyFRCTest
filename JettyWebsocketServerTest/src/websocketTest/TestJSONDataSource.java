package websocketTest;


public class TestJSONDataSource {
	
	public int TestData1;
	public double TestData2;
	public boolean TestBool;
	
	public Calibration cal1;
	public Calibration cal2;
	
	
	public void startDataGeneration(){
		cal1 = new Calibration("Cal1", 1.5, CassesroleWebStates.getCalWrangler(),-5,40.5);
		cal2 = new Calibration("Cal2",87.23, CassesroleWebStates.getCalWrangler());
		
		Thread dataGenThread = new Thread(new Runnable() {
			@Override
			public void run(){
				while(true){
					TestData1 = TestData1 - 3 + (int)cal1.get();
					TestData2 = TestData1/2.0 + 4.0 + cal2.get();
					TestBool = !TestBool;
					
					CassesroleWebStates.putInteger("Test Data #1", TestData1);
					CassesroleWebStates.putDouble("Test Data #2", TestData2);
					CassesroleWebStates.putBoolean("Test Boolean", TestBool);
					
					CassesroleWebStates.putString("Test String", "Very special things!");
					
					try {
						Thread.sleep(350);
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
