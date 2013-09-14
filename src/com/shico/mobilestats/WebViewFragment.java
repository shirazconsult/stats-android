package com.shico.mobilestats;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.Toast;

import com.shico.mobilestats.WebViewFragment.MyWebClient.EventReceiver;
import com.shico.mobilestats.adapters.GrouppedDataListAdapter;
import com.shico.mobilestats.event.ChartEvent;
import com.shico.mobilestats.loaders.LiveUsageChartDataLoader;

public class WebViewFragment extends Fragment implements OnSharedPreferenceChangeListener{
	public static final String ARG_CHART_ID = "chart_id";
	public static final String ARG_CHART_URL = "chart_url";
	
	public static final String Y_AXIS_OPTION_SUFFIX = ".yAxis";
	public static final String SCORE_NUM_OPTION_SUFFIX = ".scoreNum";
	public static final String SCORE_TYPE_OPTION_SUFFIX = ".scoreType";
	
	private String currentURL;
	private LiveUsageChartDataLoader liveUsageChartDataLoader; 
	private MyWebClient myWebClient; 
	private WebView thisWebView;
	private DisplayMetrics metrics;
	private String currentChartName;
	private String currentChartOptions;
	
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
		
		thisWebView = (WebView) v.findViewById(R.id.webview);
		thisWebView.getSettings().setJavaScriptEnabled(true);
		thisWebView.setWebViewClient(myWebClient);
		thisWebView.setWebChromeClient(new WebChromeClient());
		thisWebView.addJavascriptInterface(liveUsageChartDataLoader, "LiveUsageChartDataLoader");
		
		setGestureListener(v, thisWebView);
		
		int idx = getArguments().getInt(MainActivity.ARG_MENU_ITEM_IDX);
		switch (idx) {
		case MenuAdapter.CHARTS_MENU_IDX:
			currentChartName = getArguments().getString(MainActivity.ARG_MENU_CHART_ITEM_NAME);
			currentChartOptions = getChartOptions(PreferenceManager.getDefaultSharedPreferences(getActivity()));
			if(currentChartName != null){
				thisWebView.loadUrl("file:///android_asset/LiveUsage.html");
			}
			break;
		case MenuAdapter.HELP_MENU_IDX:
		case MenuAdapter.ABOUT_MENU_IDX:
			thisWebView.loadData(getArguments().getString("temp.html"), "text/html", null);
			break;
		default:
			throw new IllegalArgumentException("No webview for menu item idex: " + idx);
		}
		
		// Get display metrics
		metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);			
		
		return v;
	}
	
	private static final int swipe_Min_Distance = 100;
	private static final int swipe_Max_Distance = 350;
	private static final int swipe_Min_Velocity = 100;

	private void setGestureListener(View... views) {
		final GestureDetector gesture = new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				final float xDistance = Math.abs(e1.getX() - e2.getX());
				final float yDistance = Math.abs(e1.getY() - e2.getY());
				if (xDistance > swipe_Max_Distance || yDistance > swipe_Max_Distance){
					return false;
				}
				velocityX = Math.abs(velocityX);
				velocityY = Math.abs(velocityY);
				boolean result = false;
				if (velocityX > swipe_Min_Velocity
						&& xDistance > swipe_Min_Distance) {
					if (e1.getX() > e2.getX()){ // right to left
						// swipe to left
					}else{
						// swipe to right
					}
					result = true;
				} else if (velocityY > swipe_Min_Velocity
						&& yDistance > swipe_Min_Distance) {
					if (e1.getY() > e2.getY()){
						// swipe up
					}else if(e1.getY() < 200){
						// swipe down
						showSettings();
					}
					result = true;
				}
				return result;
			}
		});

		for (int i = 0; i < views.length; i++) {			
			views[i].setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return gesture.onTouchEvent(event);
				}
			});	
		}
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
			liveUsageChartDataLoader.getTopViewInBatch("/viewbatch/LiveUsage", "2013-02", "2013-05", currentChartOptions);			
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
					// chart
					view.loadUrl("javascript:drawChart()");
					
					// table
					if(isPortrait()){
						displayDataList(context);
					}
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
		
		// register BroadcastReceiver
		BroadcastReceiver eventReceiver = myWebClient.getEventReceiver();
		IntentFilter filter = ((EventReceiver)eventReceiver).getIntentFilter();
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(eventReceiver, filter);
		
		// register preference change Listener
		PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		// unregister BroadcastReceiver
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myWebClient.getEventReceiver());
		
		// unregister preference change Listener
		PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);

	}

	static class LiveUsageEventReciever extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
	}

	private ChartSettingsDialogFragment chartSettingsDialog;
	private void showSettings(){
		if(chartSettingsDialog == null){
			chartSettingsDialog = new ChartSettingsDialogFragment();
		}
		Bundle args = new Bundle();
		args.putString("chartName", currentChartName);
		chartSettingsDialog.setArguments(args);
		chartSettingsDialog.show(getFragmentManager(), "test");
	}
	
	// Table
	// ############
	private void displayDataList(Context context){
		ListView lv = (ListView)getActivity().findViewById(R.id.groupped_data_list);
		GrouppedDataListAdapter lvAdapter;
		try {
			lvAdapter = new GrouppedDataListAdapter(getActivity(), liveUsageChartDataLoader.getDataRows());
			lv.setAdapter(lvAdapter);
		} catch (JSONException e) {
			Toast.makeText(getActivity(), "Failed to retrieve table data.", Toast.LENGTH_LONG).show();
			return;
		}
	}
		
	private boolean isPortrait(){
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.startsWith(currentChartName)){
			currentChartOptions = getChartOptions(sharedPreferences);
			thisWebView.loadUrl("file:///android_asset/LiveUsage.html");
			return;
		}
		if(key.equals("host") || key.equals("port")){
			String host = sharedPreferences.getString("host", "localhost");
			int port = sharedPreferences.getInt("port", 9119);
			if(liveUsageChartDataLoader != null){
				liveUsageChartDataLoader.setHost(host);
				liveUsageChartDataLoader.setPort(port);
			}
		}
	}
	
	private String getChartOptions(SharedPreferences prefs){
		String viewersOrDuration = prefs.getString(currentChartName+Y_AXIS_OPTION_SUFFIX, "viewers");
		String topOrBottom = prefs.getString(currentChartName+SCORE_TYPE_OPTION_SUFFIX, "top");
		int num = prefs.getInt(currentChartName+SCORE_NUM_OPTION_SUFFIX, 5);
		
		return new StringBuilder(viewersOrDuration).
				append(",").append(topOrBottom.equals("bottom") ? "low" : "top").
				append(",").append(num).toString();
	}
}
