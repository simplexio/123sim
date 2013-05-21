package fi.oulu.tol.group19project;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import fi.oulu.tol.group19project.model.AbstractDevice;
import fi.oulu.tol.group19project.model.ConcreteDevice;
import fi.oulu.tol.group19project.model.ConcreteDevice.ValueType;
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
	private final static String UNITABBREVIATION = "unit-abbreviation";



	public AbstractDevice parseString(String content) throws JSONException {
		Log.d(TAG, "Starting to parse the String...");
		JSONObject object = (JSONObject) new JSONTokener(content).nextValue();
		AbstractDevice ad = handleDevice (null, object);
		Log.d(TAG, "...parsed the input string.");
		return ad;
	}


	private AbstractDevice handleDevice(AbstractDevice parent, JSONObject object) throws JSONException {
		AbstractDevice newDevice = null;
		JSONArray names = object.names();
		Log.d(TAG, "-- In handleDevice");
		if (null != parent) {
			Log.d(TAG, "-- Parent is: " + parent.getName());
		} else {
			Log.d(TAG, "-- AbstractDevice has no parent.");
		}
		if (null != names) {
			// Get the first name in the names array.
			String deviceId = null;
			for (int i = 0; i<names.length(); i++) {
				deviceId = names.getString(i);
				String elements[] = deviceId.split(":");
				Log.d(TAG, "Check if this is a Device: " + deviceId);
				if (elements[0].equalsIgnoreCase(CONTAINER) ||
						elements[0].equalsIgnoreCase(SENSOR) || elements[0].equalsIgnoreCase(ACTUATOR)) {
					JSONObject newObject = object.getJSONObject(deviceId);
					Log.d(TAG, "Try to read the device in readDevice...");
					newDevice = readDevice(elements[0], elements[1], newObject);
					if (parent != null && newDevice != null) {
						Log.d(TAG, "Adding a device to a parent device.");
						parent.add(newDevice);
					}
				}
			}
		}
		// If there was no parent (parsing started from top level)
		// make this newly created person the parent.
		// Return parent to caller.
		if (parent == null)
			parent = newDevice;
		return parent;
	}

	private AbstractDevice readDevice(String deviceContainerType, String deviceContainerId, JSONObject object) throws JSONException {

		AbstractDevice thisDevice = null;

		// Check if this object has names
		Log.d(TAG, "-- In readDevice");

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
		Double value = null;
		
		ValueType valueType = ConcreteDevice.ValueType.BINARY;
		Double minValue = null;
		Double maxValue = null;

		
		
		JSONObject stateObject = object.optJSONObject("state");
		if (null != stateObject) {
			// We have the state structure there since stateObject is not null.
			Object obj = stateObject.opt("value");
			if (null != obj) {
				// OK, we got the "value" in the "state" too. Alright!
				if (obj instanceof Boolean) {
					// OK, value was boolean, either true or false. Handle that!
					Log.d(TAG, "Boolean value: " + obj);
					boolean boolValue = (Boolean)obj;
					valueType = ConcreteDevice.ValueType.BINARY;
					if (boolValue) {
						value = 1.0;
					} else {
						value = 0.0;
					}
					minValue = 0.0;
					maxValue = 1.0;
				} else if (obj instanceof Double) {
					// OK now we know "value" was decimal: "value" : 21.1 for example.
					valueType = ConcreteDevice.ValueType.DECIMAL;
					Log.d(TAG, "Decimal value information found on device");
					double val = (Double)obj;
					Log.d(TAG, "Value of state is: "+val);
					value = Double.valueOf(val);
					if (value == Double.NaN) {
						value = null;
					}
					// Let's check if we got the range array:
					JSONArray array = stateObject.optJSONArray("range");
					if (null != array) {
						// YES!!! Range array is THERE!!!
						val = array.getDouble(0);
						minValue = Double.valueOf(val);
						if (minValue == Double.NaN) {
							minValue = null;
						}
						val = array.getDouble(1);
						maxValue = Double.valueOf(val);
						if (maxValue == Double.NaN) {
							maxValue = null;
						}
					}
				} else {
					Log.d(TAG, "Something odd found!?: " +obj);
				}
			}
			// Then the unit of the value:
			unit = stateObject.optString("unit");
			if (unit.length() == 0) {
				unit = null;
			}
			unitabbreviation = stateObject.optString("unit-abbreviation");
			if (unitabbreviation.length() == 0) {
				unitabbreviation = null;
			}
		} else {
			Log.d(TAG, "No state information in device");
		}

	

	if (deviceContainerType.equalsIgnoreCase(CONTAINER)) {
		Log.d(TAG, "Creating a parent: " + name + " and trying to parse children");
		thisDevice= new DeviceContainer(null, name, null, description, null);
		handleDevice(thisDevice, object);
	} else if (deviceContainerType.equalsIgnoreCase(SENSOR) || deviceContainerType.equalsIgnoreCase(ACTUATOR)) {
		Log.d(TAG, "Creating a child: " + name);

		thisDevice = new ConcreteDevice(null, null, name, null, description, null, null, value, value, value, unitabbreviation);
	} else {
		// Not supported.
		throw new JSONException("Invalid JSON structure");
	}

	


	return thisDevice;
}



}



