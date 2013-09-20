package com.shico.mobilestats;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.IntentFilter;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.shico.mobilestats.event.ChartEvent;

public class MovieRentWebViewFragment extends WebViewFragment {	
	private static final String EVENT_TYPE = "movieRent";
	protected static final int FIRST_PAGE_WITH_GROUPED_COLUMN_CHART = 0;
	protected static final int SECOND_PAGE_WITH_SIMPLE_COLUMN_CHART = 1;
	protected static final int THIRD_PAGE_WITH_PIE_CHART = 2;
	
	private static final String GROUPED_COLUMN_CHART_HTML = "MovieRent_grouped_columnchart.html";
	private static final String SIMPLE_COLUMN_CHART_HTML = "MovieRent_simple_columnchart.html";
	private static final String PIE_CHART_HTML = "MovieRent_piechart.html";
	
	private Map<Integer, String> viewPageHtmlMap;
	
	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(this, "MovieRentWebView");
	}

	@Override
	protected Map<Integer, String> getViewPageHtmlMap() {
		if(viewPageHtmlMap == null){
			viewPageHtmlMap = new HashMap<Integer, String>();
			viewPageHtmlMap.put(FIRST_PAGE_WITH_GROUPED_COLUMN_CHART, GROUPED_COLUMN_CHART_HTML);
			viewPageHtmlMap.put(SECOND_PAGE_WITH_SIMPLE_COLUMN_CHART, SIMPLE_COLUMN_CHART_HTML);
			viewPageHtmlMap.put(THIRD_PAGE_WITH_PIE_CHART, PIE_CHART_HTML);
		}
		return viewPageHtmlMap;
	}

	@Override
	public IntentFilter getBroadcastReceiverFilter() {
		IntentFilter filter = new IntentFilter(ChartEvent.STATS_EVENT_DATA);
		switch(viewpage){
		case FIRST_PAGE_WITH_GROUPED_COLUMN_CHART:
			filter.addCategory("/viewbatch/"+EVENT_TYPE);
			break;
		default:
			filter.addCategory("/view/"+EVENT_TYPE);			
		}
		return filter;
	}
	
	protected void loadChartData() {
		switch(viewpage){
		case FIRST_PAGE_WITH_GROUPED_COLUMN_CHART:
			getChartDataLoader().getTopViewInBatch("/viewbatch/"+EVENT_TYPE, "2013-02", "2013-05", getLoadOptions());
			break;
		default:
			getChartDataLoader().getTopView("/view/"+EVENT_TYPE, "2013-02", "2013-05", getLoadOptions());
		}		
	}
		
	@JavascriptInterface
	public String getOptions() throws JSONException{
		switch(viewpage){
		case FIRST_PAGE_WITH_GROUPED_COLUMN_CHART:
			return new JSONObject("{title: 'Movie Rents', "+ 	 
					"hAxis: {title: 'Time'}, "+
					"vAxis: {title: 'Total Movie Rents'}}").toString();
		case SECOND_PAGE_WITH_SIMPLE_COLUMN_CHART:
			return new JSONObject("{title: 'Movie Rents', "+ 	 
					"vAxis: {title: 'Total Movie Rents'}}").toString();			
		default:
			return new JSONObject(
					"{title: 'Movie Rents - Total Rentals'," +
					"is3D: true}").toString();
		}
	} 	
}
