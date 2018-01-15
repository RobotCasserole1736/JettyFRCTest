package org.usfirst.frc.team1736.lib.WebServer;

import java.util.Hashtable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DriverViewAutoSelector extends DriverViewObject {
	
    /** JSON object for initializing the  */
    private JSONObject asJsonInitObj;
    
    private JSONObject asJsonUpdateObj;
    
    /** String for keeping track of present value */
    private String current_id;
    
    String name;
	String[] selection_ids;
	static volatile Hashtable<String, String> idToNameTable = new Hashtable<String, String>();
	
	
	public DriverViewAutoSelector(String name_in, String[] options){
		name = name_in;
		
		selection_ids = new String[options.length];
		
		for(int i = 0; i < options.length; i++) {
			selection_ids[i] = Utils.nameTransform(options[i]);
			idToNameTable.put(selection_ids[i], options[i]);
		}

        // Create new objects
		asJsonInitObj = new JSONObject();
		asJsonInitObj.put("type", "autosel");
		asJsonInitObj.put("name", name_in);
		
		JSONArray optsArrObj = new JSONArray();
		for(String id : selection_ids) {
			JSONObject tmp = new JSONObject();
			tmp.put("id", id);
			tmp.put("displayName", idToNameTable.get(id));
			optsArrObj.add(tmp);
		}
		asJsonInitObj.put("options",optsArrObj);
		
	    //Create the JSON object for defining the update data for the autoshift selector.
		//Curently does nothing.
	    asJsonUpdateObj = new JSONObject();
	    asJsonUpdateObj.put("type", "autosel");
	    asJsonUpdateObj.put("name", name);
		
	}

	@Override
	public JSONObject getInitJsonObj() {
		return asJsonInitObj;
	}
	
	@Override
	public void setCommandObj(Object cmd_in) {
		current_id = (String) cmd_in;
	}
	
	public String getNameFromId(String id) {
		return idToNameTable.get(id);
	}

	public String getVal(String name) {
		if(current_id != null)
			return getNameFromId(current_id);
		else
			return null;

	}

	@Override
	public JSONObject getUpdJsonObj() {
		return asJsonUpdateObj;
	}


	@Override
	public String getName() {
		return name;
	}


}