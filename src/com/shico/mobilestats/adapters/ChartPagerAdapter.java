package com.shico.mobilestats.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.shico.mobilestats.LiveUsageWebViewFragment;
import com.shico.mobilestats.MainActivity;
import com.shico.mobilestats.MovieRentWebViewFragment;
import com.shico.mobilestats.WebViewFragment;
import com.shico.mobilestats.WidgetShowWebViewFragment;

public class ChartPagerAdapter extends FragmentPagerAdapter {
	private String chartName;
	
	public ChartPagerAdapter(FragmentManager fm, String chartName) {
		super(fm);
		this.chartName = chartName;
	}

	@Override
	public Fragment getItem(int page) {
		Bundle args = new Bundle();
		args.putString(MainActivity.ARG_MENU_CHART_ITEM_NAME, chartName);
		args.putInt(WebViewFragment.ARG_CHART_VIEWPAGE, page);
		
		WebViewFragment fragment = null;
		if(chartName.equalsIgnoreCase("channels")){				
			fragment = new LiveUsageWebViewFragment();
		}else if(chartName.equalsIgnoreCase("movies")){
			fragment = new MovieRentWebViewFragment();
		}else if(chartName.equalsIgnoreCase("programs")){
//			Toast.makeText(getActivity(), "No view for "+currentChartName+" is implemented yet.", Toast.LENGTH_LONG).show();				
		}else if(chartName.equalsIgnoreCase("widgets")){
			fragment = new WidgetShowWebViewFragment();
		}
		
		fragment.setArguments(args);
		return fragment;		
	}

	@Override
	public int getCount() {
		return 2;
	}

}
