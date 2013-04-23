package fi.oulu.tol.group19project;

import fi.oulu.tol.group19project.HomeControlService.HomeControlBinder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class DeviceListActivity extends ListActivity {


	private static final String TAG = "Group19HomeControl";
	public static final int DEBUG = 3;
	private HomeControlService mControlService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DeviceAdapter adapter = DeviceAdapter.getInstance();
		adapter.setInflater(getLayoutInflater());
		this.setListAdapter(adapter);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		Intent intent = new Intent(this, HomeControlService.class);
		startService(intent);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Device device = (Device)DeviceAdapter.getInstance().getItem(position);
		if (null != device) {
			Intent intent = new Intent(this, DeviceActivity.class);
			intent.putExtra(DeviceActivity.KEY_DEVICE_NAME, device.getName());
			this.startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {

		Intent intent = new Intent(this, SettingsActivity.class);
		this.startActivity(intent);
		return false;
	}


	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, HomeControlService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}


	@Override
	protected void onStop() {
		super.onStop();
		new CountDownTimer(20000, 1000) {

			public void onTick(long millisUntilFinished) {

			}

			public void onFinish() {
				NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(DeviceListActivity.this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Notification")
				.setContentText("The application has stopped!");
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(DeviceListActivity.this, DeviceListActivity.class);

				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(DeviceListActivity.this);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(DeviceListActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
						stackBuilder.getPendingIntent(
								0,
								PendingIntent.FLAG_UPDATE_CURRENT
								);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
						(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(0, mBuilder.build());
			}
		}.start();
		if (mControlService != null) {
			unbindService(mServiceConnection);
			mControlService = null;
		}
	}

	public void onRefreshButtonClick() {
		Log.d(TAG, String.valueOf(DEBUG));
	}


	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			HomeControlBinder HomeControlBinder = (HomeControlBinder)binder;
			mControlService = HomeControlBinder.getService();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mControlService = null;
		}
	};

}
