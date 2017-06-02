package org.usfirst.frc.team1736.lib.WebServer;

public class PlotSample {
	double value;
	double time;
	
	public PlotSample(double val_in, double time_in){
		value = val_in;
		time = time_in;
	}
	
	public double getTime_sec(){
		return time;
	}
	
	public double getVal(){
		return value;
	}

}
