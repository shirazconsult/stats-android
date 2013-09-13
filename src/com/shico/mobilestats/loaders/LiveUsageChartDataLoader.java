package com.shico.mobilestats.loaders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

import com.shico.mobilestats.event.ChartEvent;

public class LiveUsageChartDataLoader extends ChartDataLoader {
	private JSONObject dataTable;
	private JSONObject options;
	
	public LiveUsageChartDataLoader(Context context) throws JSONException {
		super(context);
	}

	@Override
	public JSONObject getDataTable() throws JSONException {
		if(dataTable == null){
			dataTable = new JSONObject();
			dataTable.put("cols", topViewColumns);
			dataTable.put("rows", new JSONArray());
		}
		return dataTable;
	}

	@Override
	protected String getIntentAction() {
		return ChartEvent.LIVE_USAGE_DATA;
	}

	// Javascript interface methods
	@JavascriptInterface
	public String getData(){
		return dataTable.toString();
	}
	@JavascriptInterface
	public String getOptions() throws JSONException{
		return new JSONObject("{title: 'Live Usage', "+ 	 
			"hAxis: {title: 'Time'}, "+
			"vAxis: {title: 'Viewers'}}").toString();
    };
}
