package com.shico.mobilestats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.shico.mobilestats.WebViewFragment.MyWebClient.EventReceiver;
import com.shico.mobilestats.event.ChartEvent;
import com.shico.mobilestats.loaders.ChartDataLoader;
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
		
		setGestureListener(v, wv);
		
		int idx = getArguments().getInt(MainActivity.ARG_MENU_ITEM_IDX);
		switch (idx) {
		case MenuAdapter.CHARTS_MENU_IDX:
			String chartName = getArguments().getString(MainActivity.ARG_MENU_CHART_ITEM_NAME);
			if(chartName != null){
				wv.loadUrl("file:///android_asset/LiveUsage.html");
			}
			break;
		case MenuAdapter.HELP_MENU_IDX:
		case MenuAdapter.ABOUT_MENU_IDX:
			wv.loadData(getArguments().getString("temp.html"), "text/html", null);
			break;
		default:
			throw new IllegalArgumentException("No webview for menu item idex: " + idx);
		}
		
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
				System.out.println(" ######### onFling");
				final float xDistance = Math.abs(e1.getX() - e2.getX());
				final float yDistance = Math.abs(e1.getY() - e2.getY());
				if (xDistance > swipe_Max_Distance || yDistance > swipe_Max_Distance){
					return false;
				}
				System.out.println(" ## Still in onFling");
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
					System.out.println(" ## Still in onFling>> Checking the metrics");					
					if (e1.getY() > e2.getY()){ // bottom to up
						DisplayMetrics metrics = new DisplayMetrics();
						getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
						System.out.println("################## "+e1.getY()+" , "+e2.getY()+" ##### "+metrics.heightPixels);
						showSettings();
					}else{
						// swipe down
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
					// chart
					view.loadUrl("javascript:drawChart()");
					
					// table
					if(isPortrait()){
						displayDataTable(context);
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

	private void showSettings(){
		ChartSettingsDialogFragment csdf = new ChartSettingsDialogFragment();
		csdf.show(getFragmentManager(), "test");
	}
	
	// Table
	// ############
	private LayoutParams tableRowLayoutParams = null;
	private void displayDataTable(Context context){
		TableLayout table = (TableLayout) getActivity().findViewById(R.id.chart_table);
		try {
			JSONObject jsonTable = liveUsageChartDataLoader.getDataTable();
			JSONArray rows = jsonTable.getJSONArray("rows");
			

			// header
			tableRowLayoutParams = (isPortrait() 
					? new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
					: new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			table.addView(buildHeader(context)); 
	        			
			// rows
			for(int i=0; i<rows.length(); i++) {
				table.addView(buildRow(context, i, rows.getJSONArray(i)));
			}
			
			if(isPortrait()){
				table.setColumnStretchable(0, true);
			}
		} catch (JSONException e) {
			Log.e("Failed to retrieve datatable object from liveUsageChartDataLoader.", e.getMessage());
			Toast.makeText(context, "Error in displaying data as table.", Toast.LENGTH_SHORT).show();
			return;						
		}
		
	}
	
	private TableRow buildHeader(Context ctx){
		TableRow header = new TableRow(ctx);
		header.setId(10);
		header.setBackgroundColor(Color.GRAY);
		header.setLayoutParams(tableRowLayoutParams);
		
		header.addView(buildTextView(ctx, 0, "Name"));
		header.addView(buildTextView(ctx, 1, "Viewers"));
		header.addView(buildTextView(ctx, 2, "Duration"));
		header.addView(buildTextView(ctx, 3, "Time"));
		
		return header;
	}

	private TableRow buildRow(Context ctx, int rowNum, JSONArray jsonRow) throws JSONException{
		TableRow row = new TableRow(ctx);
		int rowId = (rowNum+1)*10;
		row.setId(rowId);
		if(rowNum % 2 != 0){
			row.setBackgroundColor(Color.GRAY);
		}
		row.setLayoutParams(tableRowLayoutParams);

		row.addView(buildTextView(ctx, rowId+1, jsonRow.getString(ChartDataLoader.nameIdx)));
		row.addView(buildTextView(ctx, rowId+2, jsonRow.getString(ChartDataLoader.viewersIdx)));
		try{
			long duration = jsonRow.getLong(ChartDataLoader.durationIdx);
			row.addView(buildTextView(ctx, rowId+3, duration/60000+" min."));
		}catch(JSONException e){			
			row.addView(buildTextView(ctx, rowId+3, ""));
		}
		row.addView(buildTextView(ctx, rowId+4, jsonRow.getString(ChartDataLoader.timeIdx)));

		return row;
	}
	
	private TextView buildTextView(Context ctx, int id, String value){
		TextView tv = new TextView(ctx);
        tv.setId(0);
        tv.setText(value);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(20, 5, 20, 5);
        
        return tv;
	}
	
	private boolean isPortrait(){
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}
}
