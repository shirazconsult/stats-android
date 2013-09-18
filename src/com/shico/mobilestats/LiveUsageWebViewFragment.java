package com.shico.mobilestats;

import java.util.HashMap;
import java.util.Map;

import android.webkit.WebView;

public class LiveUsageWebViewFragment extends WebViewFragment {	
	private static final String EVENT_TYPE = "LiveUsage";
	private static final String COLUMN_CHART_HTML_VIEWERS = "LiveUsage_columnchart_viewers.html";
	private static final String COLUMN_CHART_HTML_DURATION = "LiveUsage_columnchart_duration.html";
	private static final String PIE_CHART_HTML = "LiveUsage_piechart.html";
	
	private Map<Integer, String> viewPageHtmlMap;

	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(this, "LiveUsageWebView");
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

}
