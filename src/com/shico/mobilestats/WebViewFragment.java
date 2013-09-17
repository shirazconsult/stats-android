package com.shico.mobilestats;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
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

import com.shico.mobilestats.adapters.GrouppedDataListAdapter;
import com.shico.mobilestats.event.ChartEvent;
import com.shico.mobilestats.loaders.ChartDataLoader;
import com.shico.mobilestats.settings.ChartSettingsDialogFragment;

public abstract class WebViewFragment extends Fragment implements OnSharedPreferenceChangeListener{
	public static final String ARG_CHART_ID = "chart_id";
	public static final String ARG_CHART_URL = "chart_url";
	public final static String ARG_CHART_VIEWPAGE = "chart.viewpage";
	public static final String CHART_NAME = "chart_name";
	private static final String CHART_OPTIONS = "chart_options";
	
	public static final String Y_AXIS_OPTION_SUFFIX = ".yAxis";
	public static final String SCORE_NUM_OPTION_SUFFIX = ".scoreNum";
	public static final String SCORE_TYPE_OPTION_SUFFIX = ".scoreType";
	
	protected String currentURL;
	protected MyWebClient myWebClient; 
	protected WebView thisWebView;
	protected ListView thisListView;
	private DisplayMetrics metrics;
	protected String currentChartName;
	protected String currentChartOptions;
//	protected boolean groupView;
	private ProgressDialog progressDiag;

	protected abstract ChartDataLoader getChartDataLoader();
	protected abstract void addJavascriptInterface(WebView view);
	protected abstract String getColumnChartViewHtml();
	protected abstract String getChartEventType();
	
	// specific methods for view pages (fragments of same type but with different views)
	protected abstract void loadData();
	protected abstract IntentFilter getBroadcastReceiverFilter();
	
	protected int viewpage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myWebClient = new MyWebClient();
		if(savedInstanceState != null){
			currentChartName = savedInstanceState.getString(CHART_NAME);
			currentChartOptions = savedInstanceState.getString(CHART_OPTIONS);
			currentURL = savedInstanceState.getString(ARG_CHART_URL);
			viewpage = savedInstanceState.getInt(ARG_CHART_VIEWPAGE);
			
			getChartDataLoader().onRestoreInstanceState(savedInstanceState);
		}		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CHART_NAME, currentChartName);
		outState.putString(CHART_OPTIONS, currentChartOptions);
		outState.putString(ARG_CHART_URL, currentURL);		
		outState.putInt(ARG_CHART_VIEWPAGE, viewpage);
		
		getChartDataLoader().onSaveInstanceState(outState);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.webview, container, false);
		
		thisListView = (ListView) v.findViewById(R.id.groupped_data_list);
		thisWebView = (WebView) v.findViewById(R.id.webview);
		thisWebView.getSettings().setJavaScriptEnabled(true);
		thisWebView.setWebViewClient(myWebClient);
		thisWebView.setWebChromeClient(new WebChromeClient());
		addJavascriptInterface(thisWebView);
		
		setGestureListener(v, thisWebView);
		
		currentChartName = getArguments().getString(MainActivity.ARG_MENU_CHART_ITEM_NAME);
		viewpage = getArguments().getInt(ARG_CHART_VIEWPAGE);
		updateCurrentChartOptions(PreferenceManager.getDefaultSharedPreferences(getActivity()));
		if(currentChartName != null){
			loadChartView();
		}
		
		// Get display metrics
		metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);			
		
		return v;
	}
	
//	private void showSecondChartFragment() {
//		Bundle args = new Bundle();
//		args.putString(MainActivity.ARG_MENU_CHART_ITEM_NAME, currentChartName);
//		args.putBoolean(WebViewFragment.ARG_CHART_GROUP_VIEW, false);
//
//		WebViewFragment secondFragment = null;
//		if(currentChartName.equalsIgnoreCase("channels")){				
//			secondFragment = new LiveUsageWebViewFragment();
//		}else if(currentChartName.equalsIgnoreCase("movies")){
//			secondFragment = new MovieRentWebViewFragment();
//		}else if(currentChartName.equalsIgnoreCase("programs")){
//			Toast.makeText(getActivity(), "No view for "+currentChartName+" is implemented yet.", Toast.LENGTH_LONG).show();				
//		}else if(currentChartName.equalsIgnoreCase("widgets")){
//			secondFragment = new WidgetShowWebViewFragment();
//		}
//
//		secondFragment.setArguments(args);
//
//		FragmentManager fragmentManager = getFragmentManager();
//		fragmentManager.beginTransaction()
//				.replace(R.id.content_frame, secondFragment).commit();
//
//	}

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
//						showSecondChartFragment();
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
			if(progressDiag != null){
				progressDiag.setMessage("Loading statistics data...:"+viewpage);
			}
			loadData();
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
					try{
						if(progressDiag != null){
							progressDiag.setMessage("Drawing chart...:"+viewpage);
						}
						
						Log.d("WebViewFragment", viewpage+": Received event. Drawing chart and table."+intent.toString());
						// chart					
						view.loadUrl("javascript:drawChart()");
					
						// table
						if(isPortrait()){
							displayDataList(context);
						}
					}finally{
						progressDiag.dismiss();
					}
				}
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
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(eventReceiver, getBroadcastReceiverFilter());
		
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

	private ChartSettingsDialogFragment chartSettingsDialog;
	private void showSettings(){
		if(chartSettingsDialog == null){
			chartSettingsDialog = new ChartSettingsDialogFragment();
		}
		Bundle args = new Bundle();
		args.putString(CHART_NAME, currentChartName);
		chartSettingsDialog.setArguments(args);
		chartSettingsDialog.show(getFragmentManager(), "test");
	}
	
	private void displayDataList(Context context){
		GrouppedDataListAdapter lvAdapter;
		try {
			lvAdapter = new GrouppedDataListAdapter(getActivity(), getChartDataLoader().getDataRows());
			thisListView.setAdapter(lvAdapter);
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
			updateCurrentChartOptions(sharedPreferences);
			loadChartView();
			return;
		}
		if(key.equals("host") || key.equals("port")){
			String host = sharedPreferences.getString("host", "localhost");
			int port = sharedPreferences.getInt("port", 9119);
			if(getChartDataLoader() != null){
				getChartDataLoader().setHost(host);
				getChartDataLoader().setPort(port);
			}
		}
	}
	
	private String updateCurrentChartOptions(SharedPreferences prefs){
		String viewersOrDuration = prefs.getString(currentChartName+Y_AXIS_OPTION_SUFFIX, "viewers");
		String topOrBottom = prefs.getString(currentChartName+SCORE_TYPE_OPTION_SUFFIX, "top");
		int num = prefs.getInt(currentChartName+SCORE_NUM_OPTION_SUFFIX, 5);
		
		currentChartOptions = new StringBuilder(viewersOrDuration).
				append(",").append(topOrBottom.equals("bottom") ? "low" : "top").
				append(",").append(num).toString();
		
		Log.d("WebViewFragment", viewpage+": Updating current chart options to: "+currentChartOptions);
		return currentChartOptions;
	}
	
	private void loadChartView(){
		progressDiag = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL); 
		progressDiag.setMessage(viewpage+": Loading chart library ...");
		progressDiag.show();
		Log.d("WebViewFragment", viewpage+": Loading page: "+getColumnChartViewHtml());
		thisWebView.loadUrl("file:///android_asset/"+getColumnChartViewHtml());
	}
}
