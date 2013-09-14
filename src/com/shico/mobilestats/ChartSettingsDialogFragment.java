package com.shico.mobilestats;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.RadioGroup;

public class ChartSettingsDialogFragment extends DialogFragment {
	public final static String DEFAULT_Y_AXIS_OPTION = "viewers";
	public final static String DEFAULT_SCORE_TYPE_OPTION = "top";
	public final static int DEFAULT_SCORE_NUM_OPTION = 5;

	private View view;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogSlideAnim);
		LayoutInflater inflator = getActivity().getLayoutInflater();
		
		view = inflator.inflate(R.layout.chart_settings, null);
		builder.setView(view).
			setPositiveButton(R.string.fetch, new OnClickListener() {		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Bundle arguments = getArguments();
					String chartName = arguments.getString("chartName");					
					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
					
					View thisView = ChartSettingsDialogFragment.this.view;
					RadioGroup rg = (RadioGroup)thisView.findViewById(R.id.radio_gr_yaxis);
					if(rg.getCheckedRadioButtonId() == R.id.radio_btn_viewer){						
						editor.putString(chartName+WebViewFragment.Y_AXIS_OPTION_SUFFIX, "viewers");
					}else{
						editor.putString(chartName+WebViewFragment.Y_AXIS_OPTION_SUFFIX, "duration");
					}
					rg = (RadioGroup)thisView.findViewById(R.id.radio_gr_score_num);
					switch(rg.getCheckedRadioButtonId()){
					case(R.id.radio_btn_3):
						editor.putInt(chartName+WebViewFragment.SCORE_NUM_OPTION_SUFFIX, 3);
						break;
					case(R.id.radio_btn_5):
						editor.putInt(chartName+WebViewFragment.SCORE_NUM_OPTION_SUFFIX, 5);
						break;
					case(R.id.radio_btn_10):
						editor.putInt(chartName+WebViewFragment.SCORE_NUM_OPTION_SUFFIX, 10);
						break;					
					}
					rg = (RadioGroup)thisView.findViewById(R.id.radio_gr_score_type);
					if(rg.getCheckedRadioButtonId() == R.id.radio_btn_top){
						editor.putString(chartName+WebViewFragment.SCORE_TYPE_OPTION_SUFFIX, "top");
					}else{
						editor.putString(chartName+WebViewFragment.SCORE_TYPE_OPTION_SUFFIX, "bottom");
					}
					editor.commit();
				}
			}).
			setNegativeButton(R.string.cancel, new OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		
		AlertDialog dialog = builder.create();
		LayoutParams params = dialog.getWindow().getAttributes();
		params.gravity = Gravity.TOP | Gravity.CENTER;
				
		return dialog;
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle arguments = getArguments();
		String chartName = arguments.getString("chartName");
		readChartPreferences(view, chartName);
	}


	private void readChartPreferences(View view, String chartName){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		String yAxisOption = prefs.getString(chartName+WebViewFragment.Y_AXIS_OPTION_SUFFIX, DEFAULT_Y_AXIS_OPTION);
		RadioGroup rg = (RadioGroup)view.findViewById(R.id.radio_gr_yaxis);
		if(yAxisOption.equals("viewers")){
			rg.check(R.id.radio_btn_viewer);
		}else{
			rg.check(R.id.radio_btn_duration);			
		}
		
		String scoreType = prefs.getString(chartName+WebViewFragment.SCORE_TYPE_OPTION_SUFFIX, DEFAULT_SCORE_TYPE_OPTION);
		rg = (RadioGroup)view.findViewById(R.id.radio_gr_score_type);
		if(scoreType.equals("top")){
			rg.check(R.id.radio_btn_top);
		}else{
			rg.check(R.id.radio_btn_bottom);			
		}
		
		int scoreNum = prefs.getInt(chartName+WebViewFragment.SCORE_NUM_OPTION_SUFFIX, DEFAULT_SCORE_NUM_OPTION);
		rg = (RadioGroup)view.findViewById(R.id.radio_gr_score_num);
		switch(scoreNum){
		case 3: 
			rg.check(R.id.radio_btn_3);
			break;
		case 5:
			rg.check(R.id.radio_btn_5);
			break;
		case 10:
			rg.check(R.id.radio_btn_10);
			break;
		}
	}
}
