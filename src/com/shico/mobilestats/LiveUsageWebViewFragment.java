package com.shico.mobilestats;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.IntentFilter;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.shico.mobilestats.event.ChartEvent;

public class LiveUsageWebViewFragment extends WebViewFragment {	
	private static final String EVENT_TYPE = "LiveUsage";
	private static final String COLUMN_CHART_HTML_VIEWERS = "LiveUsage.html";
	private static final String COLUMN_CHART_HTML_DURATION = "LiveUsage_2.html";
	private static final String PIE_CHART_HTML = "LiveUsage_pie_chart.html";
	private static final int PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS = 0;
	private static final int SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION = 1;
	private static final int TERNARY_PAGE_WITH_PIE_CHART = 2;	
	
	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(this, "LiveUsageWebView");
	}

	@Override
	protected String getColumnChartViewHtml() {
		switch(viewpage){
		case PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS:
			return COLUMN_CHART_HTML_VIEWERS;
		case SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION:
			return COLUMN_CHART_HTML_DURATION;
		case TERNARY_PAGE_WITH_PIE_CHART:
			return PIE_CHART_HTML;
		}
		throw new IllegalStateException("ViewPage is out of range.");
	}

	@Override
	protected void loadData() {
		switch(viewpage){
		case PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS:
			getChartDataLoader().getTopViewInBatch("/viewbatch/"+EVENT_TYPE, "2013-02", "2013-05", getLoadOptions());
			break;
		case SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION:
			getChartDataLoader().getTopViewInBatch("/viewbatch/"+EVENT_TYPE, "2013-02", "2013-05", getLoadOptions());
			break;
		case TERNARY_PAGE_WITH_PIE_CHART:
			getChartDataLoader().getTopViewInBatch("/view/"+EVENT_TYPE, "2013-02", "2013-05", getLoadOptions());
			break;
		default:
			throw new IllegalStateException("ViewPage is out of range.");
		}		
	}

	@Override
	protected IntentFilter getBroadcastReceiverFilter() {
		IntentFilter filter = new IntentFilter(ChartEvent.STATS_EVENT_DATA);
		if(viewpage != TERNARY_PAGE_WITH_PIE_CHART){
			filter.addCategory("/viewbatch/"+EVENT_TYPE);
		}else{
			filter.addCategory("/view/"+EVENT_TYPE);
		}
		return filter;
	}
	
	@Override
	protected boolean match(Intent intent) {
		String loadOptions = (String)intent.getExtras().get(ChartEvent.DATA_LOAD_OPTIONS);
		return getLoadOptions().equals(loadOptions);
	}

	private String getLoadOptions(){
		switch(viewpage){
		case PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS:
			String options = currentChartOptions;
			if(currentChartOptions.startsWith("duration")){
				options = "viewers"+currentChartOptions.substring(currentChartOptions.indexOf(','));
			}
			return options;
		case SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION:
			options = currentChartOptions;
			if(currentChartOptions.startsWith("viewers")){
				options = "duration"+currentChartOptions.substring(currentChartOptions.indexOf(','));
			}
			return options;
		case TERNARY_PAGE_WITH_PIE_CHART:
		default:
			return currentChartOptions;
		}
	}
	
	// Javascript interface methods
	@JavascriptInterface
	public String getData(){
		return getChartDataLoader().getCurrentDataTable().toString();
	}
	
	@JavascriptInterface
	public String getOptions() throws JSONException{
		switch(viewpage){
		case PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS:
			return new JSONObject("{title: 'Live Usage', "+ 	 
					"hAxis: {title: 'Time'}, "+
					"vAxis: {title: 'Viewers'}}").toString();
		case SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION:
			return new JSONObject("{title: 'Live Usage', "+ 	 
					"hAxis: {title: 'Time'}, "+
					"vAxis: {title: 'Duration'}}").toString();
		case TERNARY_PAGE_WITH_PIE_CHART:
		default:
			return new JSONObject("{title: 'Live Usage'}").toString();
		}
	
	}    

}
