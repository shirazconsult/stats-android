package com.shico.mobilestats;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.IntentFilter;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.shico.mobilestats.event.ChartEvent;

public class LiveUsageWebViewFragment extends WebViewFragment {	
	private static final String EVENT_TYPE = "LiveUsage";
	protected static final int FIRST_PAGE_WITH_COLUMN_CHART_VIEWERS = 0;
	protected static final int SECOND_PAGE_WITH_COLUMN_CHART_DURATION = 1;
	protected static final int THIRD_PAGE_WITH_SIMPLE_COLUMN_CHART_VIEWERS = 2;
	protected static final int FOURTH_PAGE_WITH_PIE_CHART_VIEWERS = 3;	
	protected static final int FIFTH_PAGE_WITH_SIMPLE_COLUMN_CHART_DURATION = 4;
	protected static final int SIXTH_PAGE_WITH_PIE_CHART_DURATION = 5;	
	
	private static final String COLUMN_CHART_HTML_VIEWERS = "LiveUsage_columnchart_viewers.html";
	private static final String COLUMN_CHART_HTML_DURATION = "LiveUsage_columnchart_duration.html";
	private static final String SIMPLE_COLUMN_CHART_HTML_VIEWERS = "LiveUsage_simple_columnchart_viewers.html";
	private static final String SIMPLE_COLUMN_CHART_HTML_DURATION = "LiveUsage_simple_columnchart_duration.html";
	private static final String PIE_CHART_HTML_VIEWERS = "LiveUsage_piechart_duration.html";
	private static final String PIE_CHART_HTML_DURATION = "LiveUsage_piechart_duration.html";
	
	private Map<Integer, String> viewPageHtmlMap;

	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(this, "LiveUsageWebView");
	}
	
	@Override
	public IntentFilter getBroadcastReceiverFilter() {
		IntentFilter filter = new IntentFilter(ChartEvent.STATS_EVENT_DATA);
		switch(viewpage){
		case FIRST_PAGE_WITH_COLUMN_CHART_VIEWERS:
		case SECOND_PAGE_WITH_COLUMN_CHART_DURATION:
			filter.addCategory("/viewbatch/"+EVENT_TYPE);
			break;
		default:
			filter.addCategory("/view/"+EVENT_TYPE);
		}
		return filter;
	}

	@Override
	protected Map<Integer, String> getViewPageHtmlMap() {
		if(viewPageHtmlMap == null){
			viewPageHtmlMap = new HashMap<Integer, String>();
			viewPageHtmlMap.put(FIRST_PAGE_WITH_COLUMN_CHART_VIEWERS, COLUMN_CHART_HTML_VIEWERS);
			viewPageHtmlMap.put(SECOND_PAGE_WITH_COLUMN_CHART_DURATION, COLUMN_CHART_HTML_DURATION);
			viewPageHtmlMap.put(THIRD_PAGE_WITH_SIMPLE_COLUMN_CHART_VIEWERS, SIMPLE_COLUMN_CHART_HTML_VIEWERS);
			viewPageHtmlMap.put(FOURTH_PAGE_WITH_PIE_CHART_VIEWERS, PIE_CHART_HTML_VIEWERS);
			viewPageHtmlMap.put(FIFTH_PAGE_WITH_SIMPLE_COLUMN_CHART_DURATION, SIMPLE_COLUMN_CHART_HTML_DURATION);
			viewPageHtmlMap.put(SIXTH_PAGE_WITH_PIE_CHART_DURATION, PIE_CHART_HTML_DURATION);
		}
		return viewPageHtmlMap;
	}
	
	@Override
	protected void loadChartData() {
		switch(viewpage){
		case FIRST_PAGE_WITH_COLUMN_CHART_VIEWERS:
		case SECOND_PAGE_WITH_COLUMN_CHART_DURATION:
			getChartDataLoader().getTopViewInBatch("/viewbatch/"+EVENT_TYPE, "2013-02", "2013-05", getLoadOptions());
			break;
		default:
			getChartDataLoader().getTopView("/view/"+EVENT_TYPE, "2013-02", "2013-05", getLoadOptions());
		}		
	}

	@Override
	public String getLoadOptions(){
		switch(viewpage){
		case FIRST_PAGE_WITH_COLUMN_CHART_VIEWERS:
			String options = currentChartOptions;
			if(currentChartOptions.startsWith("duration")){
				options = "viewers"+currentChartOptions.substring(currentChartOptions.indexOf(','));
			}
			return options;
		case SECOND_PAGE_WITH_COLUMN_CHART_DURATION:
			options = currentChartOptions;
			if(currentChartOptions.startsWith("viewers")){
				options = "duration"+currentChartOptions.substring(currentChartOptions.indexOf(','));
			}
			return options;
		default:
			return currentChartOptions;
		}
	}
	
	// chart options. called by web resources
	@JavascriptInterface
	public String getOptions() throws JSONException{
		switch(viewpage){
		case FIRST_PAGE_WITH_COLUMN_CHART_VIEWERS:
			return new JSONObject("{title: 'TV Channels', "+ 	 
					"hAxis: {title: 'Time'}, "+
					"vAxis: {title: 'Total Number of Viewers'}}").toString();
		case SECOND_PAGE_WITH_COLUMN_CHART_DURATION:
			return new JSONObject("{title: 'TV Channels', "+ 	 
					"hAxis: {title: 'Time'}, "+
					"vAxis: {title: 'Total Watched Hours'}}").toString();
		case THIRD_PAGE_WITH_SIMPLE_COLUMN_CHART_VIEWERS:
			return new JSONObject(
					"{title: 'TV Channels'," +
					"vAxis: {title: 'Total Number of Viewers'}}").toString();
		case FOURTH_PAGE_WITH_PIE_CHART_VIEWERS:
			return new JSONObject(
					"{title: 'TV Channels - Total Number of Viewers'," +
					"is3D: true}").toString();
		case FIFTH_PAGE_WITH_SIMPLE_COLUMN_CHART_DURATION:
			return new JSONObject(
					"{title: 'TV Channels'," +
					"vAxis: {title: 'Total Watched Hours'}}").toString();
		case SIXTH_PAGE_WITH_PIE_CHART_DURATION:
			return new JSONObject(
					"{title: 'TV Channels - Total Watched Hours'," +
					"is3D: true}").toString();
		default:
			return new JSONObject("{title: 'TV Channels'}").toString();
		}
	}  
	

}
