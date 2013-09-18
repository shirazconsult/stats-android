package com.shico.mobilestats;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WidgetShowWebViewFragment extends WebViewFragment {	
	private static final String EVENT_TYPE = "widgetShow";	
	private static final String COLUMN_CHART_HTML_VIEWERS = "WidgetShow_columnchart_viewers.html";
	private static final String COLUMN_CHART_HTML_DURATION = "WidgetShow_columnchart_duration.html";
	private static final String PIE_CHART_HTML = "WidgetShow_piechart.html";
	
	private Map<Integer, String> viewPageHtmlMap;

	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(this, "WidgetShowWebView");
	}
	
	@Override
	protected String getEventType() {
		return EVENT_TYPE;
	}

	@Override
	protected Map<Integer, String> getViewPageHtmlMap() {
		if(viewPageHtmlMap == null){
			viewPageHtmlMap = new HashMap<Integer, String>();
			viewPageHtmlMap.put(PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS, COLUMN_CHART_HTML_VIEWERS);
			viewPageHtmlMap.put(SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION, COLUMN_CHART_HTML_DURATION);
			viewPageHtmlMap.put(TERNARY_PAGE_WITH_PIE_CHART, PIE_CHART_HTML);
		}
		return viewPageHtmlMap;
	}
	
	@JavascriptInterface
	public String getOptions() throws JSONException{
		switch(viewpage){
		case PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS:
			return new JSONObject("{title: 'Widget Activations', "+ 	 
					"hAxis: {title: 'Time'}, "+
					"vAxis: {title: 'Total Widget Activations'}}").toString();
		case SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION:
		case TERNARY_PAGE_WITH_PIE_CHART:
		default:
			return new JSONObject("{title: 'Widget Activations'}").toString();
		}
	}  
	
}
