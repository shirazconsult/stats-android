package com.shico.mobilestats;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
//		Preference settingsDialog = getPreferenceScreen().findPreference("global_settings");
//		settingsDialog.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {			
//			@Override
//			public boolean onPreferenceChange(Preference preference, Object newValue) {
//				restartThis();				
//				return false;
//			}
//		});
	}

	private void restartThis() {
		Activity activity = getActivity();
	    activity.finish();
	    activity.overridePendingTransition(0, 0);
	    activity.startActivity(activity.getIntent());
	    activity.overridePendingTransition(0, 0);
	}

}
