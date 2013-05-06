package fi.oulu.tol.group19project;

import fi.oulu.tol.group19project.model.AbstractDevice;
import fi.oulu.tol.group19project.model.ConcreteDevice;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ScrollView;
import android.widget.TextView;

public class DeviceActivity extends Activity {

	private AbstractDevice device = null;
	public static final String KEY_DEVICE_ID = "device-id";

	
	protected void onCreate(Bundle savedInstanceState, int position) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		//Get the device name from the extras, placed there by the launching activity.
		// Note that getExtras could return null if no extras would be there. Also getString,
		// if no such string extra would be there. Checking these would be a good idea.
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			String id = this.getIntent().getExtras().getString(KEY_DEVICE_ID);
			// Get the device object, using the name and the singleton adapter.
			device = (AbstractDevice)DeviceAdapter.getInstance().getDevice(id);
			if (null != device) {
				if (device.getType() == AbstractDevice.Type.ACTUATOR || device.getType() == AbstractDevice.Type.SENSOR) {
					if (((ConcreteDevice)device).getValueType() == ConcreteDevice.ValueType.BINARY) {
						//For sensors, show data only in read-only widgets, or set the widget disabled by calling setEnabled(false).
						if (device.getType() == AbstractDevice.Type.SENSOR) {
							//setContentView(R.layout.activity_device
							((TextView)findViewById(R.id.name_item)).setText(device.getName());
							((TextView)findViewById(R.id.description_item)).setText(device.getDescription());
							}
						//For actuators, you show data values with an editable widget (slider, editor, on/off button,...), with setEnabled(true) which is of course on by default.
						else {

						}
					}
					else if (((ConcreteDevice)device).getValueType() == ConcreteDevice.ValueType.DECIMAL) {
						if (device.getType() == AbstractDevice.Type.SENSOR) {
							((TextView)findViewById(R.id.name_item)).setText(device.getName());
							((TextView)findViewById(R.id.description_item)).setText(device.getDescription());
						}
						//For actuators, you show data values with an editable widget (slider, editor, on/off button,...), with setEnabled(true) which is of course on by default.
						else {

						}
					}
				}
				//For containers, you just show the basic description of the container, plus possibly how many child devices it has (just a number is enough).
				else {
					((TextView)findViewById(R.id.description_item)).setText(device.getDescription());
								}
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
