package org.usfirst.frc.team1736.lib.DataServer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.json.simple.JSONObject;

public class Signal {

	String id;
	String display_name;
	String units;
	
	LinkedList<DataSample> samples;
	HashSet<AcqSpec> acqSpecs;
	
	
	/**
	 * Class which describes one line on a plot
	 * @param name_in String of what to call the signal (human readable)
	 * @param units_in units the signal is in.
	 */
	public Signal(String name_in, String units_in){
		display_name = name_in;
		id = IDGen.makeUUID(name_in);
		units = units_in;
		
		samples = new LinkedList<DataSample>();
		acqSpecs = new HashSet<AcqSpec>();
		
		CasseroleDataServer.getInstance().registerSignal(this);
	}
	
	
	/**
	 * Adds a new sample to the signal queue. It is intended that
	 * the controls code would call this once per loop to add a new
	 * datapoint to the real-time graph.
	 * @param time_in
	 * @param value_in
	 */
	public void addSample(double time_in, double value_in){
		if(dataSamplingNeeded()){
			samples.add(new DataSample(time_in, value_in));
		}
	}
	
	
	/**
	 * Register an acquisition spec with the signal. This allows the signal to know
	 * how long to hang on to its samples before discarding.
	 * @param spec_in
	 */
	public void addAcqSpec(AcqSpec spec_in) {
		if(!acqSpecs.contains(spec_in)) {
			acqSpecs.add(spec_in);	
		} else {
			System.out.println("Warning: DataServer: Cannot add AcqSpec " + spec_in.toString() + " - it has already been added to signal " + id);
		}

	}
	
	
	/**
	 * Remove an acquisition spec from the signal. Indicates an acqList no longer needs
	 * to know about the samples in this signal. 
	 * @param spec_in
	 */
	public void rmAcqSpec(AcqSpec spec_in) {
		if(acqSpecs.contains(spec_in)) {
			acqSpecs.remove(spec_in);	
		} else {
			System.out.println("Warning: DataServer: Cannot remove AcqSpec " + spec_in.toString() + " - it is not in signal " + id);
		}
	}
	
	
	/**
	 * Returns an array of all the samples currently in the queue, and then clears it.
	 * It is intended that the webserver would call this to transmit all available 
	 * data from previous iterations. 
	 */
	public DataSample[] getSamples(AcqSpec spec_in, double req_time_ms){
		int max_size = samples.size();
		ArrayList<DataSample> retval = new ArrayList<DataSample>(max_size);
		
		int i = samples.size()-1;
		
		while(i >= 0 && samples.get(i).getSampleTime_ms() >= req_time_ms - spec_in.getTxRate_ms() ) {
			
			if(spec_in.getSamplePeriod_ms() > 0) {
				/* Need to downsample the data returned */
				if(samples.get(i).getSampleTime_ms() >= spec_in.getTimestampOfMostRecentTXedSample_ms() + spec_in.getSamplePeriod_ms() ) {
					/* time to snag another sample */
					retval.add(0, samples.get(i));
					spec_in.setTimestampOfMostRecentTXedSample_ms(samples.get(i).getSampleTime_ms());
				}
			} else {
				/* return samples at native period */
				retval.add(0, samples.get(i));
			}
			
			i--;
		}

		DataSample[] retArray = new DataSample[retval.size()];
		retval.toArray(retArray);
		return retArray;
	}
	
	
	/**
	 * Discard samples which are too old to be useful
	 */
	public void trimSamples(double time_now_ms){
		double oldest_rqd_timestamp = time_now_ms;
		int trim_start_idx = 0;
		
		if(!dataSamplingNeeded()) {
			/* If nothing is being sampled, clear it all out and do nothing else */
			forceClearSamples();
			return;
		}
		
		/* Determine, amongst all acq specs registered, the oldest sample that should be maintained */
		for(AcqSpec spec : acqSpecs) {
			oldest_rqd_timestamp = Math.min(spec.getOldestRqdTimestamp_ms(time_now_ms), oldest_rqd_timestamp);
		}
		
		/* Go through the samples in the list until we find one which should be trimmed */
		for(trim_start_idx = samples.size() - 1; trim_start_idx >= 0; trim_start_idx--) {
			if(samples.get(trim_start_idx).getSampleTime_ms() < oldest_rqd_timestamp) {
				break;
			}
		}
		
		/* Remove this sample and all prior ones */
		for(int i = trim_start_idx; i >= 0; i--)
		{
			samples.remove(i);
		}
		
		return;
	}
	
	/**
	 * Discards all samples from the buffer
	 */
	public void forceClearSamples(){
		samples.clear();
	}
	
	/**
	 * @return The name of the signal
	 */
	public String getID(){
		return id;
	}
	
	/**
	 * @return The User-friendly name of the signal
	 */
	public String getDisplayName(){
		return display_name;
	}
	
	/**
	 * @return The name of the units the signal is measured in.
	 */
	public String getUnits(){
		return units;
	}
	
	boolean dataSamplingNeeded() {
		return acqSpecs.size() > 0;
	}
	
	public JSONObject getJSONProperties() {
		JSONObject retval = new JSONObject();
		retval.put("id", id);
		retval.put("display_name", display_name);
		retval.put("units", units);
		return retval;
	}
}
