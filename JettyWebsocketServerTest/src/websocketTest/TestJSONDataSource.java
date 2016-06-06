package websocketTest;


public class TestJSONDataSource {
	
	public int TestData1;
	public double TestData2;
	public boolean TestBool;
	
	
	public void startDataGeneration(){
		Thread dataGenThread = new Thread(new Runnable() {
			@Override
			public void run(){
				while(true){
					TestData1 = TestData1 - 3;
					TestData2 = TestData1/2.0 + 4.0;
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
