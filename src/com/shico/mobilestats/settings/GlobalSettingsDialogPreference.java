package com.shico.mobilestats.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.shico.mobilestats.R;

public class GlobalSettingsDialogPreference extends DialogPreference {
	protected Context context;

	public GlobalSettingsDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	
	@Override
	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		
		if(which == DialogInterface.BUTTON_POSITIVE){
			PreferenceManager.getDefaultSharedPreferences(this.context).edit().clear().commit();
			PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
			getOnPreferenceChangeListener().onPreferenceChange(this, true);
			
			Toast.makeText(context, "Saved preferences", Toast.LENGTH_SHORT).show();
		}
	}	
}
