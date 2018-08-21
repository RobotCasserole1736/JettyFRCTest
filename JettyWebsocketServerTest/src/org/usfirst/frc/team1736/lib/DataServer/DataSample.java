package org.usfirst.frc.team1736.lib.DataServer;

public class DataSample {
	double value;
	double sample_time_ms;
	
	public DataSample(double time_ms_in, double val_in){
		value = val_in;
		sample_time_ms = time_ms_in;
	}
	
	public double getSampleTime_ms(){
		return sample_time_ms;
	}
	
	public double getVal(){
		return value;
	}

}
