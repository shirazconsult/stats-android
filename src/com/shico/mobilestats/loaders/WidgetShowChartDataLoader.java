package com.shico.mobilestats.loaders;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WidgetShowChartDataLoader extends ChartDataLoader {
	private JSONObject options;
	
	public WidgetShowChartDataLoader(Context context) throws JSONException {
		super(context);
	}

	// Javascript interface methods
	@JavascriptInterface
	public String getData(){
		return getCurrentDataTable().toString();
	}
	@JavascriptInterface
	public String getOptions() throws JSONException{
		return new JSONObject("{title: 'Movie Rent', "+ 	 
			"hAxis: {title: 'Time'}, "+
			"vAxis: {title: 'Viewers'}}").toString();
    }    
}
