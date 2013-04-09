package fi.oulu.tol.group19project;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class DeviceListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DeviceAdapter adapter = DeviceAdapter.getInstance();
		adapter.setInflater(getLayoutInflater());
		this.setListAdapter(adapter);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
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

		Device device = (Device)DeviceAdapter.getInstance().getItem(position);
		if (null != device) {
			Intent intent = new Intent(this, SettingsActivity.class);
			intent.putExtra(DeviceActivity.KEY_DEVICE_NAME, device.getName());
			this.startActivity(intent);
		}
		return false;
	}

}
