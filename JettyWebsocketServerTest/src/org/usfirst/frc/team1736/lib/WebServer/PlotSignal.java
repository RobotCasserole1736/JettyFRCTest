package org.usfirst.frc.team1736.lib.WebServer;

import java.util.LinkedList;
import java.util.Queue;

public class PlotSignal {

	String name;
	String display_name;
	String units;
	
	Queue<PlotSample> sample_queue;
	
	/**
	 * Class which describes one line on a plot
	 * @param name_in String of what to call the signal
	 * @param units_in units the signal is in.
	 */
	public PlotSignal(String name_in, String units_in){
		display_name = name_in;
		name = Utils.nameTransform(name_in);
		units = units_in;
		
		sample_queue = new LinkedList<PlotSample>();
	}
	
	/**
	 * Adds a new sample to the signal queue. It is intended that
	 * the controls code would call this once per loop to add a new
	 * datapoint to the real-time graph.
	 * @param time_in
	 * @param value_in
	 */
	public void addSample(double time_in, double value_in){
		sample_queue.add(new PlotSample(time_in, value_in));
	}
	
	/**
	 * Returns an array of all the samples currently in the queue, and then clears it.
	 * It is intended that the weberver would call this to transmit all available 
	 * data from previous iterations. This might return null if the control code
	 * has no new data.
	 */
	public PlotSample[] getAllSamples(){
		int size = sample_queue.size();
		PlotSample[] retval;
		if(size > 0){
			retval = new PlotSample[size];
			sample_queue.toArray(retval);
			sample_queue.clear();
		} else {
			retval = null;
		}
		return retval;
	}
}
