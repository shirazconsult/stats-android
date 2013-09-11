package com.shico.mobilestats;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class ResetDialogPreference extends DialogPreference {
	protected Context context;
	
	public ResetDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		
		if(which == DialogInterface.BUTTON_POSITIVE){
			PreferenceManager.getDefaultSharedPreferences(this.context).edit().clear().commit();
			PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
			getOnPreferenceChangeListener().onPreferenceChange(this, true);
		}
	}	
}