package com.shico.mobilestats;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class ChartSettingsDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogSlideAnim);
		LayoutInflater inflator = getActivity().getLayoutInflater();
		
		builder.setView(inflator.inflate(R.layout.chart_settings, null)).
			setPositiveButton(R.string.fetch, new OnClickListener() {		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getActivity(), "FETCHING...", Toast.LENGTH_SHORT).show();
				}
			}).
			setNegativeButton(R.string.cancel, new OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getActivity(), "CANCELING...", Toast.LENGTH_SHORT).show();
				}
			});
		
		AlertDialog dialog = builder.create();
		LayoutParams params = dialog.getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM | Gravity.CENTER;
		return dialog;
	}

}
