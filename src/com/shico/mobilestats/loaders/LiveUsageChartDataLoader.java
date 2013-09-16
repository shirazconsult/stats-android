package com.shico.mobilestats.loaders;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.shico.mobilestats.event.ChartEvent;

public class LiveUsageChartDataLoader extends ChartDataLoader {
	private JSONObject options;
	
	public LiveUsageChartDataLoader(Context context) throws JSONException {
		super(context);
	}

	@Override
	protected String getIntentAction() {
		return ChartEvent.LIVE_USAGE_DATA;
	}

	// Javascript interface methods
	@JavascriptInterface
	public String getData(){
		return currentDataTable.toString();
	}
	@JavascriptInterface
	public String getOptions() throws JSONException{
		return new JSONObject("{title: 'Live Usage', "+ 	 
			"hAxis: {title: 'Time'}, "+
			"vAxis: {title: 'Viewers'}}").toString();
    }    
}
