package com.shico.mobilestats.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.shico.mobilestats.LiveUsageWebViewFragment;
import com.shico.mobilestats.MainActivity;
import com.shico.mobilestats.MovieRentWebViewFragment;
import com.shico.mobilestats.WebViewFragment;
import com.shico.mobilestats.WidgetShowWebViewFragment;

public class ChartPagerAdapter extends FragmentPagerAdapter {
	private String chartName;
	private int chartId;
	private Map<String, List<Integer>> chartPageMap = new HashMap<String, List<Integer>>();

	public ChartPagerAdapter(FragmentManager fm, String chartName, int chartId) {
		super(fm);
		setChartName(chartName);
		this.chartId = chartId;
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
	
	private void printPageMap(String msg){
		StringBuilder sb = new StringBuilder(msg).append(" : ");
		for (Entry<String, List<Integer>> entry : chartPageMap.entrySet()) {
			sb.append(entry.getKey()).append("=");
			for (int page : entry.getValue()) {				
				sb.append(page).append(",");
			}
			sb.append(";;");
		}
		Log.d("ChartPageAdapter", sb.toString());
	}
	
	@Override
	public int getCount() {
		if(chartName.equalsIgnoreCase("channels")){
			return 6;
		}
		return 3;
	}

	@Override
	public long getItemId(int position) {
		return (chartId*100)+position;
	}	

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public void setChartId(int chartId) {
		this.chartId = chartId;
	}

	@Override
	public void startUpdate(ViewGroup container) {
		// TODO Auto-generated method stub
		super.startUpdate(container);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		// TODO Auto-generated method stub
		super.finishUpdate(container);
//		viewpager.setCurrentItem(0);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return super.isViewFromObject(view, object);
	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return super.saveState();
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		// TODO Auto-generated method stub
		super.restoreState(state, loader);
	}

	private void addToPageMap(String chart, int page){
		if(chartPageMap.get(chart) == null){
			ArrayList<Integer> pageList = new ArrayList<Integer>();
			chartPageMap.put(chart, pageList);
		}
		chartPageMap.get(chart).add(page);
	}
	
	private void removeFromPageMap(String chart, int page){
		List<Integer> pageList = chartPageMap.get(chart);
		if(pageList != null){
			for (int i=0; i<pageList.size(); i++) {
				if(pageList.get(i) == page){
					pageList.remove(i);
				}
			}
		}
	}
	
}
