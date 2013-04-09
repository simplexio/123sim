package fi.oulu.tol.group19project;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsActivity extends Activity {

	private SettingsFragment settingsFragment = null;
	public static final String KEY_PREF_CONNECT_TO_SERVER_SETTING = "connect_to_server_check";
	public static final String KEY_PREF_SERVER_ADDRESS = "server_address";

	public void onCreate(Bundle savedInstanceState) {
		settingsFragment = new SettingsFragment();

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, settingsFragment).commit();
	}

	public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
			if (key.equals(KEY_PREF_CONNECT_TO_SERVER_SETTING)) {
				CheckBoxPreference preference = (CheckBoxPreference)findPreference(key);
				if (preference != null) {
					boolean connectionPref = pref.getBoolean(key, false);
					if (connectionPref) {
						preference.setSummary(preference.getSummaryOn());
					} else {
						preference.setSummary(preference.getSummaryOff());
					}
				}
			} else if (key.equals(KEY_PREF_SERVER_ADDRESS)) {
				EditTextPreference preference = (EditTextPreference)findPreference(key);
				preference.setSummary(pref.getString(key, ""));
			}
		}
		
		public void onPause(SharedPreferences sharedPref, OnSharedPreferenceChangeListener listener) {
			sharedPref.unregisterOnSharedPreferenceChangeListener(listener);
		}
		
		public void onResume() {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			sharedPref.registerOnSharedPreferenceChangeListener(settingsFragment);
		}

	}


}
