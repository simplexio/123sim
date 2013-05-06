package fi.oulu.tol.group19project;

import fi.oulu.tol.group19project.model.AbstractDevice;
import fi.oulu.tol.group19project.model.DeviceContainer;
import fi.oulu.tol.group19project.model.DeviceCounter;
import fi.oulu.tol.group19project.model.DeviceFetcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	public void setInflater(LayoutInflater mInflater) {
		this.mInflater = mInflater;
	}

	private static DeviceAdapter instance = null;
	private DeviceContainer devices = null;
	private DeviceCounter counter = new DeviceCounter();
	DeviceFetcher fetcher = new DeviceFetcher();

	public static DeviceAdapter getInstance() {
		if (null == instance) {
			instance = new DeviceAdapter();
		}
		return instance;
	}


	@Override
	public int getCount() {
		if (null != devices) {
			return counter.startCounting(devices);
		}
		return 0;
	}

	public Object getItem(int position) {
		if (null != devices) {
			AbstractDevice d = fetcher.fetchChildDevice(devices, position);
			return d;
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the device from the position'th place from the vechiles vector.
		AbstractDevice d = fetcher.fetchChildDevice(devices, position);
		ViewHolder holder = null;
		if (convertView == null){
			// Since convertView is null, we cannot reuse already created view
			// but have to inflate one from the xml layout file. Inflate the 
			// sensor row layout, if the type of v is Type.SENSOR, else if it is 
			// Type.ACTUATOR, then inflate the xml layout for the actuator.
			if (d.getType() == AbstractDevice.Type.SENSOR) {
				convertView = mInflater.inflate(R.layout.row_sensor_layout, null); 
			} else if (d.getType() == AbstractDevice.Type.ACTUATOR) {
				convertView = mInflater.inflate(R.layout.row_actuator_layout, null); 
			} else {
				convertView = mInflater.inflate(R.layout.row_container_layout, null);
			}
			// Create a viewholder utility object (see below and the Google I/O video!),
			// and use it to create the row view and it's elements from the layout file.
			// Cache the holder object to the row view using the setTag() of convertView.
			holder = new ViewHolder(convertView); 
			holder.nameView = holder.getName();
			holder.descriptionView = holder.getDescription();
			convertView.setTag(holder);
		} else {
			// Yes! convertView provides us a reusable view object! It contains - in the tag -
			// the cached viewholder object (created in the if branch above).
			// Use convertView.getTag() to get access to the viewholder there.
			holder = (ViewHolder)convertView.getTag();
		}    
		if (d != null && holder != null ) {
			holder.nameView.setText(d.getName());
			holder.descriptionView.setText(d.getDescription());
			// Put the device's name to the viewholder's name property.
			// Put the device's description to the viewholder's description property.
		}
		// return the convertView, which was either reused or created, depending.
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
			return 3; // we have three different view types (rows).
	}

	/*Change the DeviceAdapter.getItemViewType: now it shows different row layouts for a sensor and actuator. 
	Change it so that it uses one view type for containers and another for sensors and actuators. 
	Make any necessary changes to the row layout xml files.*/
	@Override
	public int getItemViewType(int position) {
		if (null != devices) {
			AbstractDevice d=fetcher.fetchChildDevice(devices, position);
			if (d.getType() == AbstractDevice.Type.SENSOR) {
				return 0;  // the type zero is the sensor row view for sensor objects.
			} else if (d.getType() == AbstractDevice.Type.ACTUATOR){
				return 1;  // the type one is the actuator row view for actuator objects.
			} else {
				return 2;
			}

		}
		return 0;
	}

	private class ViewHolder {
		private View row;
		private TextView nameView = null;
		private TextView descriptionView = null;
		private ScrollView dataView = null;

		public ViewHolder(View row) {
			this.row = row;
		}

		public TextView getName() {
			if(nameView== null) {
				nameView = (TextView) row.findViewById(R.id.name_item);
			}
			return nameView;
		}     

		public TextView getDescription() {
			if(descriptionView == null) {
				descriptionView = (TextView) row.findViewById(R.id.description_item);
			}
			return descriptionView;
		}		
	}

	public AbstractDevice getDevice(String id) {
		AbstractDevice d = fetcher.fetchChildDevice(devices, id);
		return d;
	}


	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	public void setDevices(DeviceContainer devices) {
		this.devices = devices;
	}



}
