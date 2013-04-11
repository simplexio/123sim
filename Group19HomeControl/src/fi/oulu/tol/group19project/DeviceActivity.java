package fi.oulu.tol.group19project;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class DeviceActivity extends Activity {

	private Device device = null;
	public static final String KEY_DEVICE_NAME = "device-name"; // Key used in launching the activity, above.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);

		// Get the device name from the extras, placed there by the launching activity.
		// Note that getExtras could return null if no extras would be there. Also getString,
		// if no such string extra would be there. Checking these would be a good idea.
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			String name = extras.getString(KEY_DEVICE_NAME);
			// Get the device object, using the name and the singleton adapter.
			device = (Device)DeviceAdapter.getInstance().getDevice(name);
			if (null != device) {
				// Then finally get the device data and put it to the text views.
				((TextView)findViewById(R.id.name_item)).setText(device.getName());
				((TextView)findViewById(R.id.description_item)).setText(device.getDescription());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device, menu);
		return true;
	}
}
