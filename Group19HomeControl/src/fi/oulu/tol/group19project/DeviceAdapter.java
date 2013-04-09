package fi.oulu.tol.group19project;

import java.util.Vector;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {

	// Add this as a member variable to your adapter class:
	private LayoutInflater mInflater = null;

	// Add this as a method to your adapter class:
	public void setInflater(LayoutInflater mInflater) {
		this.mInflater = mInflater;
	}

	private Vector<Device> devices = new Vector<Device>();
	private static DeviceAdapter instance = null;

	private DeviceAdapter() {
		initialize();
	}

	public static DeviceAdapter getInstance() {
		if (null == instance) {
			instance = new DeviceAdapter();
		}
		return instance;
	}

	private void initialize() {
		Device.Type types[] = { Type.SENSOR, Type.ACTUATOR, Type.SENSOR, Type.SENSOR, Type.ACTUATOR, Type.SENSOR };
		String names[] = { "Door sensor", "Door lock", "Kitchen temp", "Bedroom temp", "Bedroom heater", "Burglar alarm" };
		String descriptions[] = { "Front door", "Back door", "Temperature on the floor level", "Midair temp", "Controls temperature", "Window sensors" };


		for (int counter = 0; counter < names.length; counter++) {
			devices.add(new Device(types[counter], names[counter], descriptions[counter]));
		}
	}
	@Override
	public int getCount() {
		return devices.size();
		// TODO Auto-generated method stub
	}

	@Override
	public Object getItem(int position) {
		Device v = devices.get(position);
		return v;

	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the device from the position'th place from the vechiles vector.
		Device v = devices.get(position);
		ViewHolder holder = null;
		if (convertView == null){
			// Since convertView is null, we cannot reuse already created view
			// but have to inflate one from the xml layout file. Inflate the 
			// sensor row layout, if the type of v is Type.SENSOR, else if it is 
			// Type.ACTUATOR, then inflate the xml layout for the actuator.
			if (v.getType() == Device.Type.SENSOR) {
				convertView = mInflater.inflate(R.layout.row_sensor_layout, null); 
			} else if (v.getType() == Device.Type.ACTUATOR) {
				convertView = mInflater.inflate(R.layout.row_actuator_layout, null); 
			} else {
				// should not be possible! Throw an exception?
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
		if (v != null && holder != null ) {
			holder.nameView.setText(v.getName());
			holder.descriptionView.setText(v.getDescription());
			// Put the device's name to the viewholder's name property.
			// Put the device's description to the viewholder's description property.
			// This is left for YOU to implement!!!
		}
		// return the convertView, which was either reused or created, depending.
		return convertView;
	}


	@Override
	public int getViewTypeCount() {
		return 2; // we have two different view types (rows).
	}

	@Override
	public int getItemViewType(int position) {
		if (devices.get(position).getType() == Type.SENSOR) {
			return 0;  // the type zero is the sensor row view for sensor objects.
		} else {
			return 1;  // the type one is the actuator row view for actuator objects.
		}
	}


	private class ViewHolder {
		private View row;
		private TextView nameView = null;
		private TextView descriptionView = null;

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


	public Device getDevice(String name) {
		return null;

			
			
		}
		
	

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}



}
