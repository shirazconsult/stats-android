package com.shico.mobilestats;

import org.json.JSONException;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.shico.mobilestats.loaders.ChartDataLoader;
import com.shico.mobilestats.loaders.WidgetShowChartDataLoader;

public class WidgetShowWebViewFragment extends WebViewFragment {	
	private static final String COLUMN_CHART_HTML = "WidgetShow.html";
	private static final String EVENT_TYPE = "widgetShow";
	
	private WidgetShowChartDataLoader widgetShowChartDataLoader; 
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			widgetShowChartDataLoader = new WidgetShowChartDataLoader(getActivity());
			if(savedInstanceState != null){
				widgetShowChartDataLoader.onRestoreInstanceState(savedInstanceState);
			}
		} catch (JSONException e) {
			Log.e("WebViewFragment", "Failed to instantiate WidgetShowChartDataLoader.");
			Toast.makeText(getActivity(), "Failed to instantiate WidgetShowChartDataLoader.", Toast.LENGTH_LONG).show();
		}	
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		widgetShowChartDataLoader.onSaveInstanceState(outState);
	}

	@Override
	protected ChartDataLoader getChartDataLoader() {
		return widgetShowChartDataLoader;
	}

	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(widgetShowChartDataLoader, "WidgetShowChartDataLoader");
	}

	@Override
	protected String getColumnChartViewHtml() {
		return COLUMN_CHART_HTML;
	}

	@Override
	protected String getChartEventType() {
		return EVENT_TYPE;
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
}
