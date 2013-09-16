package com.shico.mobilestats;

import org.json.JSONException;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.shico.mobilestats.loaders.ChartDataLoader;
import com.shico.mobilestats.loaders.LiveUsageChartDataLoader;

public class LiveUsageWebViewFragment extends WebViewFragment {	
	private static final String COLUMN_CHART_HTML = "LiveUsage.html";
	private static final String EVENT_TYPE = "LiveUsage";
	
	private LiveUsageChartDataLoader liveUsageChartDataLoader; 
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			liveUsageChartDataLoader = new LiveUsageChartDataLoader(getActivity());
			if(savedInstanceState != null){
				liveUsageChartDataLoader.onRestoreInstanceState(savedInstanceState);
			}
		} catch (JSONException e) {
			Log.e("WebViewFragment", "Failed to instantiate LiveUsageChartDataLoader.");
			Toast.makeText(getActivity(), "Failed to instantiate LiveUsageChartDataLoader.", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		liveUsageChartDataLoader.onSaveInstanceState(outState);
	}

	@Override
	protected ChartDataLoader getChartDataLoader() {
		return liveUsageChartDataLoader;
	}

	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(liveUsageChartDataLoader, "LiveUsageChartDataLoader");
	}

	@Override
	protected String getColumnChartViewHtml() {
		return COLUMN_CHART_HTML;
	}

	@Override
	protected String getChartEventType() {
		return EVENT_TYPE;
	}
}
