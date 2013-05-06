package fi.oulu.tol.group19project;


import fi.oulu.tol.group19project.model.AbstractDevice.Type;
import fi.oulu.tol.group19project.model.ConcreteDevice;
import fi.oulu.tol.group19project.model.DeviceContainer;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class HomeControlService extends Service {


	private final HomeControlBinder binder = new HomeControlBinder();
	private DeviceContainer devices = new DeviceContainer(null, "container-1", "Test Container", null, null);

	@Override
	public void onCreate() {
		debugInitialize();
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
	devices.add(new ConcreteDevice(null, Type.SENSOR, "id-for-sersor-1", "Light sensor", "Outside in back yard", null, ConcreteDevice.ValueType.DECIMAL, 800.0, 10.0, 4000.0, "lumen"));
	DeviceContainer cont = new DeviceContainer(null, "container-2", "Restroom", "Loo for poo", null);
	cont.add(new ConcreteDevice(null, Type.ACTUATOR, "id-for-actuator-1", "Door lock", "Loo door", null, ConcreteDevice.ValueType.BINARY, 0.0, 0.0, 1.0, null));
	devices.add(cont);

	}
	
	public DeviceContainer getDevices() { 
		return devices;
	}
}
