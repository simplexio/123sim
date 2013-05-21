package fi.oulu.tol.group19project;


import org.json.JSONException;

import fi.oulu.tol.group19project.model.AbstractDevice.Type;
import fi.oulu.tol.group19project.model.ConcreteDevice;
import fi.oulu.tol.group19project.model.DeviceContainer;
import fi.oulu.tol.group19project.ohap.OHAPPathBuilder;
import fi.oulu.tol.group19project.ohap.OHAPImplementation;
import fi.oulu.tol.group19project.ohap.OHAPInterface;
import fi.oulu.tol.group19project.ohap.OHAPListener;
import fi.oulu.tol.group19project.ohap.TaskData;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class HomeControlService extends Service implements OHAPListener{
	// Create the path builder as a member of HomeControlService:
	private OHAPPathBuilder ohapBuilder = new OHAPPathBuilder();
	private final static String TAG = "DeviceParser";
	private final HomeControlBinder binder = new HomeControlBinder();
	private DeviceContainer devices = new DeviceContainer(null, "container-1", "No connection with Home", null, null);
	private OHAPParser parser = null;
	private String json = new String("{ \"container:room-1\": { \"name\": \"GF301-1\", \"description\": \"Antti's office\", \"location\": { \"latitude\": 65.058668, \"longitude\": 25.564338, \"altitude\": 100.0 }, 		\"sensor:switch-1\": { \"name\": \"Light switch\", \"description\": \"The light switch next to the door\", \"state\": { \"type\": \"binary\", \"value\": true }, \"location\": { \"latitude\": 65.058669, \"longitude\": 25.564338, \"altitude\": 100.6 }},		\"sensor:temperature-1\": { \"name\": \"Room temperature\", \"description\": \"The current temperature in the room\", \"state\": { \"type\": \"decimal\", \"value\": 21.1, \"range\": [-10.0, 60.0], \"unit\": \"Celcius\", \"unit-abbreviation\": \"C\" },\"location\": { \"latitude\": 65.058668, \"longitude\": 25.564339,\"altitude\": 101.2 } },		\"actuator:light-1\": { \"name\": \"Ceiling lamp\", \"description\": \"The fluerecent lamp in the ceiling\", \"state\": { \"type\": \"binary\", \"value\": true }, \"location\": { \"latitude\": 65.058669, \"longitude\": 25.564339, \"altitude\": 102.6  } } }}");
	private OHAPInterface protocol = null;
	private Handler eventHandler = new Handler();
	private HomeControlServiceObserver observer = null;

	public void setObserver(HomeControlServiceObserver observer) {
		this.observer = observer;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "In Service.onCreate");
		super.onCreate();
		parser = new OHAPParser();
		debugInitialize();
		devices = null;
		protocol = OHAPImplementation.getInstance();
		protocol.setObserver(this);



	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public class HomeControlBinder extends Binder {
		HomeControlService getService() {
			return HomeControlService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void debugInitialize () {
		//lauseella kutsutaan yll�olevassta json stringist� olevat tiedot
		try {
			devices = (DeviceContainer)parser.parseString(json);
		} catch (JSONException e) {
			e.printStackTrace();
			}
		}

		/*devices.add(new ConcreteDevice(null, Type.SENSOR, "id-for-sersor-1", "Light sensor", "Outside in back yard", null, ConcreteDevice.ValueType.DECIMAL, 800.0, 10.0, 4000.0, "lumen"));
	DeviceContainer cont = new DeviceContainer(null, "container-2", "Restroom", "Loo for poo", null);
	cont.add(new ConcreteDevice(null, Type.ACTUATOR, "id-for-actuator-1", "Door lock", "Loo door", null, ConcreteDevice.ValueType.BINARY, 0.0, 0.0, 1.0, null));
	devices.add(cont);

	}*/

		public DeviceContainer getDevices() { 
			return devices;
		}


		// Then call this method when the device state has changed (in DeviceActivity propably):
		public void deviceStateChanged(ConcreteDevice device) {
			String path = ohapBuilder.createPath(device, true);
			if (null != path) {
				try {
					protocol.setPath(null, path);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void sessionInitiatedSuccessfully() {
			protocol.getPath(null, "/");
			if (devices == null) {
				devices = (DeviceContainer) parser.parseString(content);
			}
			else {

				DeviceContainer newData = (DeviceContainer)parser.parseString(content);
				devices.updateValues(newData);
			}
		}

		@Override
		public void sessionEnded() {
			devices = null;

		}

		@Override
		public void contentFromServerArrived(String content) {
			if (null != observer) {
				observer.modelUpdated();
			}

		}

		@Override
		public void errorMessageFromServer(String msg) {
			// TODO Auto-generated method stub

		}

		@Override
		public void okFromServerArrived() {
			// TODO Auto-generated method stub

		}


		public OHAPInterface getProtocol() {
			return protocol;
		}
	}
