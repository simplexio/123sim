package fi.oulu.tol.group19project;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import fi.oulu.tol.group19project.model.DeviceContainer;

/*
{
	  "container:room-1": {
	    "name": "GF301-1",
	    "description": "Antti's office",
	    "location": {
	      "latitude": 65.058668,
	      "longitude": 25.564338,
	      "altitude": 100.0
	    },
	    "sensor:switch-1": {
	      "name": "Light switch",
	      "description": "The light switch next to the door",
	      "state": {
	        "type": "binary",
	        "value": true
	      },
	      "location": {
	        "latitude": 65.058669,
	        "longitude": 25.564338,
	        "altitude": 100.6
	      }
	    },
	    "sensor:temperature-1": {
	      "name": "Room temperature",
	      "description": "The current temperature in the room",
	      "state": {
	        "type": "decimal",
	        "value": 21.1,
	        "range": [-10.0, 60.0],
	        "unit": "Celcius",
	        "unit-abbreviation": "C"
	      },
	      "location": {
	        "latitude": 65.058668,
	        "longitude": 25.564339,
	        "altitude": 101.2
	      }
	    },
	    "actuator:light-1": {
	      "name": "Ceiling lamp",
	      "description": "The fluerecent lamp in the ceiling",
	      "state": {
	        "type": "binary",
	        "value": true
	      },
	      "location": {
	        "latitude": 65.058669,
	        "longitude": 25.564339,
	        "altitude": 102.6
	      }
	    }
	  }
	}

	*/

public class OHAPParser {
	private final static String TAG = "DeviceParser";
	private final static String CONTAINER= "container";
	private final static String SENSOR= "sensor";
	private final static String ACTUATOR = "actuator";

	
	private final static String NAME = "name";
	private final static String DESCRIPTION = "description";
	private final static String LOCATION = "location";
	private final static String LATITUDE = "latitude";
	private final static String LONGITUDE = "longitude";
	private final static String ALTITUDE = "altitude";
	private final static String STATE = "state";
	private final static String TYPE = "type";
	private final static String VALUE = "value";
	private final static String RANGE = "range";
	private final static String UNIT = "unit";
	private final static String UNITABBREVIATION = "unitabbreviation";
	
	public DeviceContainer parseString(String content) throws JSONException {
		Log.d(TAG, "Starting to parse the String...");
		JSONObject object = (JSONObject) new JSONTokener(content).nextValue();
		DeviceContainer dc = handleDevice (null, object);
		Log.d(TAG, "...parsed the input string.");
		return dc;
	}

	private DeviceContainer handleDevice(DeviceContainer parent, JSONObject object) throws JSONException {
		DeviceContainer newDeviceContainer = null;
		JSONArray names = object.names();
		Log.d(TAG, "-- In handleDevice");
		if (null != parent) {
			Log.d(TAG, "-- Parent is: " + parent.getName());
		} else {
			Log.d(TAG, "-- DeviceContainer has no parent.");
		}
		if (null != names) {
			// Get the first name in the names array.
			String deviceContainerId = null;
			for (int i = 0; i<names.length(); i++) {
				deviceContainerId = names.getString(i);
				String elements[] = deviceContainerId.split(":");
				Log.d(TAG, "Check if this is a DeviceContainer: " + deviceContainerId);
				if (elements[0].equalsIgnoreCase(CONTAINER) ||
						elements[0].equalsIgnoreCase(SENSOR) || elements[0].equalsIgnoreCase(ACTUATOR)) {
					JSONObject newObject = object.getJSONObject(deviceContainerId);
					Log.d(TAG, "Try to read the deviceContainer in readDeviceContainer...");
					newDeviceContainer = readDeviceContainer(elements[0], elements[1], newObject);
					if (parent != null && newDeviceContainer != null) {
						Log.d(TAG, "Adding a device to a parent device.");
						parent.add(newDeviceContainer);
					}
				}
			}
		}
		// If there was no parent (parsing started from top level)
		// make this newly created person the parent.
		// Return parent to caller.
		if (parent == null)
			parent = newDeviceContainer;
		return parent;
	}

	private DeviceContainer readDeviceContainer(String deviceContainerType, String deviceContainerId, JSONObject object) throws JSONException {

		DeviceContainer thisDeviceContainer = null;

		// Check if this object has names
		Log.d(TAG, "-- In readDeviceContainer");
		
		// Object has names so initialize data variables:
		String name = null;
		String description = null;
		Double location = null;
		Double latitude = null;
		Double longitude = null;
		Double altitude = null;
		String state = null;
		String type = null;
		Double []range = null;
		String unit = null;
		String unitabbreviation = null;
		// All these elements must be there so we use getXxx instead of optXxx.
		JSONObject nameObject = object.getJSONObject(CONTAINER);
		name = nameObject.getString(NAME);
		description = nameObject.getString(DESCRIPTION);


//		double tmpDouble = object.optDouble(AGE);
//		age = Double.valueOf(tmpDouble);
//		if (age == Double.NaN) {
//			age = null;
//		}
		
		Double value = null;
		double val = object.getDouble(VALUE);
		value = Double.valueOf(val);
		if (value == Double.NaN) {
		   value = null;
		}
		
		if (deviceContainerType.equalsIgnoreCase(CONTAINER)) {
			Log.d(TAG, "Creating a parent: " + name + " and trying to parse children");
			thisDeviceContainer= new DeviceContainer(null, name, null, description, null);
			handleDevice(thisDeviceContainer, object);
		} else if (deviceContainerType.equalsIgnoreCase(SENSOR) || deviceContainerType.equalsIgnoreCase(ACTUATOR)) {
			Log.d(TAG, "Creating a child: " + name);
			thisDeviceContainer = new DeviceContainer(null, name, null, description, null);
		} else {
			// Not supported.
			throw new JSONException("Invalid JSON structure");
		}

//		JSONArray array = object.optJSONArray(RANGE);
//		if (null != array) {
//			int tmpInt;
//			for (int i = 0; i< array.length(); i++) {
//				tmpInt = array.getInt(i);
//				thisDeviceContainer.addRange(tmpInt);
//			}
//		}

		return thisDeviceContainer;
	}


		
	}
	


