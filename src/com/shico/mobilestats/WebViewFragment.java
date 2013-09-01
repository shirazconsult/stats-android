package com.shico.mobilestats;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.shico.mobilestats.WebViewFragment.MyWebClient.EventReceiver;
import com.shico.mobilestats.event.ChartEvent;
import com.shico.mobilestats.loaders.LiveUsageChartDataLoader;

public class WebViewFragment extends Fragment {
	public static final String ARG_CHART_ID = "chart_id";
	public static final String ARG_CHART_URL = "chart_url";

	private String currentURL;
	private LiveUsageChartDataLoader liveUsageChartDataLoader; 
	private MyWebClient myWebClient; 
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.webview, container, false);
		
		try {
			myWebClient = new MyWebClient();
			liveUsageChartDataLoader = new LiveUsageChartDataLoader(getActivity());
		} catch (JSONException e) {
			Log.e("WebViewFragment", "Failed to instantiate LiveUsageChartDataLoader.");
			Toast.makeText(getActivity(), "Failed to instantiate LiveUsageChartDataLoader.", Toast.LENGTH_SHORT).show();
		}
		
		WebView wv = (WebView) v.findViewById(R.id.webview);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(myWebClient);
		wv.setWebChromeClient(new WebChromeClient());
		wv.addJavascriptInterface(liveUsageChartDataLoader, "LiveUsageChartDataLoader");
		
		int idx = getArguments().getInt(MainActivity.ARG_MENU_ITEM_IDX);
		switch (idx) {
		case MenuAdapter.CHARTS_MENU_IDX:
			String chartName = getArguments().getString(MainActivity.ARG_MENU_CHART_ITEM_NAME);
			if(chartName != null){
				wv.loadUrl("file:///android_asset/LiveUsage.html");
			}
			break;
		case MenuAdapter.SETTINGS_MENU_IDX:
		case MenuAdapter.HELP_MENU_IDX:
		case MenuAdapter.ABOUT_MENU_IDX:
			wv.loadData(getArguments().getString("temp.html"), "text/html", null);
			break;
		default:
			throw new IllegalArgumentException("No webview for menu item idex: " + idx);
		}
		
		return v;
	}

	public void updateUrl(String url) {
		Log.d("MyWebClient", "Update URL [" + url + "] - View [" + getView()
				+ "]");
		currentURL = url;
		WebView wv = (WebView) getView().findViewById(R.id.content_frame);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.loadUrl(url);
	}

	private void loadChartUrl(Bundle savedInstanceState, WebView wv) {
		currentURL = getArguments().getString(ARG_CHART_URL);
		if (currentURL == null && savedInstanceState != null) {
			currentURL = savedInstanceState.getString(ARG_CHART_URL);
		}
		wv.loadUrl(currentURL);
	}

	public class MyWebClient extends WebViewClient {
		private WebView view;
		
		private BroadcastReceiver eventReceiver;
		public MyWebClient() {
			super();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			this.view = view;
			liveUsageChartDataLoader.getTopViewInBatch("/viewbatch/LiveUsage", "2013-02", "2013-05", "viewers,top,3");			
		}
		
		// Broadcast receiver
		public class EventReceiver extends BroadcastReceiver{		
			@Override
			public void onReceive(Context context, Intent intent) {
				int status = intent.getExtras().getInt(ChartEvent.DATA_LOAD_STATUS);
				if(status == ChartEvent.FAILURE){
					Toast.makeText(context, "Failed to load data.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(view != null){
					view.loadUrl("javascript:drawChart()");
				}
			}
			
			public IntentFilter getIntentFilter(){
				return new IntentFilter(ChartEvent.LIVE_USAGE_DATA);
			}
		}
		
		public BroadcastReceiver getEventReceiver(){
			if(eventReceiver == null){
				eventReceiver = new EventReceiver();
			}
			return eventReceiver;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		BroadcastReceiver eventReceiver = myWebClient.getEventReceiver();
		IntentFilter filter = ((EventReceiver)eventReceiver).getIntentFilter();
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(eventReceiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myWebClient.getEventReceiver());
	}

	static class LiveUsageEventReciever extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
	}
}
