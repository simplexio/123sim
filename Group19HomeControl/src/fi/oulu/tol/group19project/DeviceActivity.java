package fi.oulu.tol.group19project;

import fi.oulu.tol.group19project.HomeControlService.HomeControlBinder;
import fi.oulu.tol.group19project.model.AbstractDevice;
import fi.oulu.tol.group19project.model.ConcreteDevice;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class DeviceActivity extends Activity implements OnSeekBarChangeListener {

	private AbstractDevice device = null;
	public static final String KEY_DEVICE_ID = "device-id";
	private static final String TAG = "Group19HomeControl";
	private HomeControlService homeControlService;


	protected void onCreate(Bundle savedInstanceState, int position) {
		Log.d(TAG, "In DeviceActivity.onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		// Prepare the seekbar event listener.
		SeekBar seekBar = (SeekBar) findViewById(R.id.value_slider);
		seekBar.setOnSeekBarChangeListener(this);

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
						// Set the on/off button to show on/off status of the device.
						Switch theSwitch = (Switch)findViewById(R.id.value_on_off_button);
						// Is on/off kind of value, HIDE numeric slider:	
						View v = findViewById(R.id.value_slider);
						v.setVisibility(View.GONE);
						// Value of zero means off, anything else is on:
						theSwitch.setChecked(((ConcreteDevice)device).getValue() != 0.0);
						//For sensors, show data only in read-only widgets, or set the widget disabled by calling setEnabled(false).
						if (device.getType() == AbstractDevice.Type.SENSOR) {
							// It was a sensor, so we do not allow manipulating the on/off switch.
							theSwitch.setEnabled(false);
						}
						//For actuators, you show data values with an editable widget (slider, editor, on/off button,...), with setEnabled(true) which is of course on by default.
						// And if the device is actuator, we enable the widget, since sensor values cannot be changed
						else {
							// ...and start listening to change events.
							theSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						    	 public void onCheckedChanged(
						    	        CompoundButton buttonView,
						    	        boolean isChecked) {
						    	      // If widget is now checked, we set the value to 1.
						    	      if (isChecked) {
						    	        ((ConcreteDevice)device).setValue(1.0);
						    	        if (null != homeControlService) {
						    	          homeControlService.deviceStateChanged((ConcreteDevice)device); // << tell service that device state changed.
						    	        }
						    	      // Otherwise the value is 0.
						    	      } else {
						    	        ((ConcreteDevice)device).setValue(0.0);
						    	        if (null != homeControlService) {
						    	          homeControlService.deviceStateChanged((ConcreteDevice)device); // << tell service that device state changed.
						    	        }
						    	      }}}
						         // Implement the onCheckedChangeListener here to handle the on/off check events!
						      );

						}
					}
					else if (((ConcreteDevice)device).getValueType() == ConcreteDevice.ValueType.DECIMAL) {
						Switch theSwitch = (Switch)findViewById(R.id.value_on_off_button);
						theSwitch.setVisibility(0);
						View v = findViewById(R.id.value_slider);

						// Then we get the min, max and value from device and
						// start setting up the slider and the position of the progress handle.
						double minLimit = 0;
						double maxLimit = 100;
						double value = ((ConcreteDevice)device).getValue();
						Double min = ((ConcreteDevice)device).getMinValue();
						Double max = ((ConcreteDevice)device).getMaxValue();
						// Oh, so tedius that the devices can have null values with these.. :/
						if (min != null) {
							minLimit = min.doubleValue();
						}
						if (max != null) {
							maxLimit = max.doubleValue();
						}
						// Now scale the position of the thumb, since device's min and max could be like -15,60
						// But the seekbar has min/max of 0,100:
						double progress = ((value-minLimit)/maxLimit)*100.0;
						// Then set the thumb position on the seekbar:
						seekBar.setProgress((int)progress);
						// And set the value as text to the accompanying TextView
						TextView valueText = (TextView)findViewById(R.id.value_text);
						valueText.setText(((ConcreteDevice)device).getValueInformation());
						if (device.getType() == AbstractDevice.Type.SENSOR) {
							seekBar.setEnabled(false);
							v.setEnabled(false);
						}
						//For actuators, you show data values with an editable widget (slider, editor, on/off button,...), with setEnabled(true) which is of course on by default.
						else {
							seekBar.setEnabled(true);
						}}
					}}
							
					
				//For containers, you just show the basic description of the container, plus possibly how many child devices it has (just a number is enough).
				else if (device.getType() == AbstractDevice.Type.CONTAINER) {
					((TextView)findViewById(R.id.description_item)).setText(device.getDescription());
				}}
			}
	
	
						

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// We are only interested if the slider is moved by the user.
		if (fromUser) {
			// We know we are handling a concrete device with ValueType.DECIMAL,
			// since the slider is hidden in other situations in the DeviceActivity.onCreate
			// by your code you put there!!
			ConcreteDevice concrete = (ConcreteDevice)device;
			// Get the min and max values from the device.
			Double min = concrete.getMinValue();
			Double max = concrete.getMaxValue();
			// New value based on the slider's position:
			double newValue = 0;
			// Device may have null values for the min and max, so we check these:
			double minLimit = 0;
			double maxLimit = 100;
			if (min != null) {
				minLimit = min.doubleValue();
			}
			if (max != null) {
				maxLimit = max.doubleValue();
			}
			// So if we didn't get the device's min or max, we have the defaults 0 and 100.
			// Now, scale the value of progress, considering the device's min and max values:
			newValue = minLimit+(((maxLimit-minLimit)*progress)/100.0);
			// You could also place a simple TextView under the slider, and show the actual value in that text view
			// since the slider widget does not itself show any values. That's what is done in this case:
			TextView valueText = (TextView)findViewById(R.id.name_item);
			// Then set the device's value with the newValue:
			concrete.setValue(newValue);
			// And get the value as text from the device to show it on the TextView:
			valueText.setText(concrete.getValueInformation());
			// We have changed something about the device, so set the result of the activity to OK.
			setResult(Activity.RESULT_OK);
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device, menu);
		return true;
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekbar) {
	    if (null != homeControlService) {
	        homeControlService.deviceStateChanged((ConcreteDevice)device);  // tell again that the device state changed.
	      }

	}
	private ServiceConnection mServiceConnection = new ServiceConnection() {
	    @Override
	    public void onServiceConnected(ComponentName name, IBinder service) {
	       HomeControlBinder binder = (HomeControlBinder)service;
	       homeControlService = binder.getService();
	       DeviceAdapter.getInstance().setDevices(homeControlService.getDevices()); // Wiring done
	    }
	    @Override
	    public void onServiceDisconnected(ComponentName name) {
	       DeviceAdapter.getInstance().setDevices(null);  // Unwiring done, adapter has no devices to show
	       homeControlService = null;
	     }

	};
	
	
}
	

