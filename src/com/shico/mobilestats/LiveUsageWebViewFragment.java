package com.shico.mobilestats;

import org.json.JSONException;

import android.content.IntentFilter;
import android.webkit.WebView;

import com.shico.mobilestats.event.ChartEvent;
import com.shico.mobilestats.loaders.ChartDataLoader;
import com.shico.mobilestats.loaders.LiveUsageChartDataLoader;

public class LiveUsageWebViewFragment extends WebViewFragment {	
	private static final String EVENT_TYPE = "LiveUsage";
	private static final String COLUMN_CHART_HTML_VIEWERS = "LiveUsage.html";
	private static final String COLUMN_CHART_HTML_DURATION = "LiveUsage_2.html";
	private static final String PIE_CHART_HTML = "LiveUsage_pie_chart.html";
	private static final int PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS = 1;
	private static final int SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION = 2;
	private static final int TERNARY_PAGE_WITH_PIE_CHART = 3;	
	
	private LiveUsageChartDataLoader liveUsageChartDataLoader; 
	private int viewpage = PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS;  // page-1. primary batch/groupped view page-2. seco 	
		
	@Override
	protected ChartDataLoader getChartDataLoader() {
		if(liveUsageChartDataLoader == null){
			try {
				liveUsageChartDataLoader = new LiveUsageChartDataLoader(getActivity());
			} catch (JSONException e) {
				throw new IllegalStateException("Unable to instantiate "+LiveUsageChartDataLoader.class.getName());
			}
		}
		return liveUsageChartDataLoader;
	}

	@Override
	protected void addJavascriptInterface(WebView view) {
		view.addJavascriptInterface(liveUsageChartDataLoader, "LiveUsageChartDataLoader");
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
	protected String getChartEventType() {
		return EVENT_TYPE;
	}

	@Override
	protected void loadData() {
		switch(viewpage){
		case PRIMARY_PAGE_WITH_COLUMN_CHART_VIEWERS:
			getChartDataLoader().getTopViewInBatch("/viewbatch/"+getChartEventType(), "2013-02", "2013-05", getOptions());
			break;
		case SECONDARY_PAGE_WITH_COLUMN_CHART_DURATION:
			getChartDataLoader().getTopViewInBatch("/viewbatch/"+getChartEventType(), "2013-02", "2013-05", getOptions());
			break;
		case TERNARY_PAGE_WITH_PIE_CHART:
			getChartDataLoader().getTopViewInBatch("/view/"+getChartEventType(), "2013-02", "2013-05", getOptions());
			break;
		default:
			throw new IllegalStateException("ViewPage is out of range.");
		}		
	}

	@Override
	protected IntentFilter getBroadcastReceiverFilter() {
		IntentFilter filter = new IntentFilter(ChartEvent.STATS_EVENT_DATA);
		filter.addCategory(getOptions());
		if(viewpage != TERNARY_PAGE_WITH_PIE_CHART){
			filter.addCategory(ChartEvent.GROUP_VIEW_DATA);
		}
		return filter;
	}
	
	private String getOptions(){
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
}
