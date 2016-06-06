package websocketTest;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class CassesroleWebStates {
	/** The list of objects which are broadcast. Must be volatile to ensure atomic accesses */
	static volatile List<JSONObject> data_array_elements = new ArrayList<JSONObject>();
	
	/** 
	 * Put a new state to the web interface, or update an existing one with the same name 
	 * @param name Name for the state to display.
	 * @param value Double Floating-point value to display
	 */
	public static void putDouble(String name, double value){
		putGeneric(name, Double.toString(value));
	}
	
	/** 
	 * Put a new state to the web interface, or update an existing one with the same name 
	 * @param name Name for the state to display.
	 * @param value Double Floating-point value to display
	 */
	public static void putBoolean(String name, boolean value){
		putGeneric(name, Boolean.toString(value));
	}
	
	public static void putInteger(String name, int value){
		putGeneric(name, Integer.toString(value));
	}
	
	public static void putString(String name, String value){
		putGeneric(name, value);	
	}
	
	private static void putGeneric(String name, String value){
		boolean is_new = true;
		for(JSONObject obj : data_array_elements){
			if(obj.get("name").equals(name)){
				obj.put("value", value);
				is_new = false;
			}
		}
		
		if(is_new){
			JSONObject new_obj = new JSONObject();
			new_obj.put("name", name);
			new_obj.put("value", value);
			data_array_elements.add(new_obj);
		}
	}

}
