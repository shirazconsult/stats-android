package com.shico.mobilestats;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends Fragment {
	public static final String ARG_CHART_ID = "chart_id";
	public static final String ARG_CHART_URL = "chart_url";

	private String currentURL;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.webview, container, false);

		WebView wv = (WebView) v.findViewById(R.id.webview);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new MyWebClient());
		
		int idx = getArguments().getInt(MainActivity.ARG_MENU_ITEM_IDX);
		switch (idx) {
		case MainActivity.CHARTS_MENU_IDX:
			loadChartUrl(savedInstanceState, wv);
			break;
		case MainActivity.SETTINGS_MENU_IDX:
		case MainActivity.HELP_MENU_IDX:
		case MainActivity.ABOUT_MENU_IDX:
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

	private class MyWebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}
	}

}
