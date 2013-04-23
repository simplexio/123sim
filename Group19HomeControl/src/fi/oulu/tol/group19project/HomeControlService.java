package fi.oulu.tol.group19project;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class HomeControlService extends Service {


private final HomeControlBinder binder = new HomeControlBinder();
	
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

}
