package com.shico.mobilestats;

import org.json.JSONException;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.shico.mobilestats.loaders.ChartDataLoader;
import com.shico.mobilestats.loaders.MovieRentChartDataLoader;

public class MovieRentWebViewFragment extends WebViewFragment {	
	private static final String COLUMN_CHART_HTML = "MovieRent.html";
	private static final String EVENT_TYPE = "movieRent";
	
	private MovieRentChartDataLoader movieRentChartDataLoader; 
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			movieRentChartDataLoader = new MovieRentChartDataLoader(getActivity());
			if(savedInstanceState != null){
				movieRentChartDataLoader.onRestoreInstanceState(savedInstanceState);
			}
		} catch (JSONException e) {
			Log.e("WebViewFragment", "Failed to instantiate MovieRentChartDataLoader.");
			Toast.makeText(getActivity(), "Failed to instantiate MovieRentChartDataLoader.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		movieRentChartDataLoader.onSaveInstanceState(outState);
	}

	@Override
	protected ChartDataLoader getChartDataLoader() {
		return movieRentChartDataLoader;
	}

	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(movieRentChartDataLoader, "MovieRentChartDataLoader");
	}

	@Override
	protected String getColumnChartViewHtml() {
		return COLUMN_CHART_HTML;
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IntentFilter getBroadcastReceiverFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean match(Intent intent) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
