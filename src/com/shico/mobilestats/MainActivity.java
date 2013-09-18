package com.shico.mobilestats;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.shico.mobilestats.adapters.ChartPagerAdapter;
import com.shico.mobilestats.adapters.MenuAdapter;
import com.shico.mobilestats.settings.SettingsFragment;

public class MainActivity extends Activity {
	public final static String ARG_MENU_ITEM_IDX = "menu.item.idx";
	public final static String ARG_MENU_CHART_ITEM_IDX = "menu.chart.item.idx";
	public final static String ARG_MENU_CHART_ITEM_NAME = "menu.chart.item.name";

	private DrawerLayout mDrawerLayout;
	private ExpandableListView mMenuDrawer;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerMenuTitle;
	private CharSequence mTitle;
	private String[] mDrawerMenuItems;
	
	private int lastSelectedGroupPosition;
	private int lastSelectedChildPosition;
	private String lastSelectedChildItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		mTitle = mDrawerMenuTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mMenuDrawer = (ExpandableListView) findViewById(R.id.menu_drawer);
		mDrawerMenuItems = getResources().getStringArray(R.array.menu_items);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
		mMenuDrawer.setAdapter(new MenuAdapter(this));
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			mMenuDrawer.setIndicatorBounds(25, 45);
		} else {
			mMenuDrawer.setIndicatorBoundsRelative(25, 45);
		}
		MenuItemClickListener menuItemClickListener = new MenuItemClickListener();
		mMenuDrawer.setOnChildClickListener(menuItemClickListener);
		mMenuDrawer.setOnGroupClickListener(menuItemClickListener);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerMenuTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			mMenuDrawer.setSelectedGroup(0);
		}else{
			lastSelectedGroupPosition = savedInstanceState.getInt(ARG_MENU_ITEM_IDX);
			lastSelectedChildItem = savedInstanceState.getString(ARG_MENU_CHART_ITEM_NAME);
			lastSelectedChildPosition = savedInstanceState.getInt(ARG_MENU_CHART_ITEM_IDX);
			if(lastSelectedGroupPosition == MenuAdapter.CHARTS_MENU_IDX){
				newChartViewPager(lastSelectedChildItem, lastSelectedChildPosition);				
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(ARG_MENU_ITEM_IDX, lastSelectedGroupPosition);
		outState.putString(ARG_MENU_CHART_ITEM_NAME, lastSelectedChildItem);
		outState.putInt(ARG_MENU_CHART_ITEM_IDX, lastSelectedChildPosition);
		
		super.onSaveInstanceState(outState);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private ChartPagerAdapter chartPagerAdapter;
	private ViewPager chartViewPager;
	private void newChartViewPager(String chartName, int chartId){
		if(chartViewPager == null){
			chartViewPager = (ViewPager)findViewById(R.id.chart_pager);
			chartPagerAdapter = new ChartPagerAdapter(getFragmentManager(), chartName, chartId);
			chartViewPager.setAdapter(chartPagerAdapter);
		}else{
			chartPagerAdapter.setChartName(chartName);
			chartPagerAdapter.setChartId(chartId);
			chartPagerAdapter.notifyDataSetChanged();
		}
	}
	
	/* The click listner for ListView in the navigation drawer */
	private class MenuItemClickListener implements
			ExpandableListView.OnChildClickListener,
			ExpandableListView.OnGroupClickListener {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			String[] chartItems = parent.getContext().getResources()
					.getStringArray(R.array.menu_chart_items);
			String chartName = chartItems[childPosition];

			Bundle args = new Bundle();
			args.putInt(ARG_MENU_ITEM_IDX, groupPosition);
			args.putString(ARG_MENU_CHART_ITEM_NAME, chartName);
			newChartViewPager(chartName, childPosition);

			// update selected item and title, then close the drawer
			mMenuDrawer.setItemChecked(childPosition + groupPosition + 1, true);
			setTitle(chartName);
			mDrawerLayout.closeDrawer(mMenuDrawer);

			lastSelectedGroupPosition = groupPosition;
			lastSelectedChildItem = chartName;
			lastSelectedChildPosition = childPosition;
			
			return true;
		}

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			Bundle args = new Bundle();
			Fragment fragment = null;
			switch (groupPosition) {
			case MenuAdapter.CHARTS_MENU_IDX:
				if (mMenuDrawer.isGroupExpanded(groupPosition)) {
					mMenuDrawer.collapseGroup(groupPosition);
				} else {
					mMenuDrawer.expandGroup(groupPosition);
				}
				mMenuDrawer.setItemChecked(groupPosition, true);
				return true;
			case MenuAdapter.SETTINGS_MENU_IDX:
				args.putString("temp.html",
						"<html><body><h1>Here comes Settings.</h1></body></html>");
				fragment = new SettingsFragment();
				break;
			case MenuAdapter.HELP_MENU_IDX:
				args.putString("temp.html",
						"<html><body><h1>Here comes Help/Manual info.</h1></body></html>");
//				fragment = new WebViewFragment();
				break;
			case MenuAdapter.ABOUT_MENU_IDX:
				args.putString("temp.html",
						"<html><body><h1>Here comes About info.</h1></body></html>");
//				fragment = new WebViewFragment();
				break;
			default:
				throw new IllegalArgumentException("No such menu item.");
			}
			args.putInt(ARG_MENU_ITEM_IDX, groupPosition);

			setFragment(args, fragment);				
			
			// update selected item and title, then close the drawer
			mMenuDrawer.collapseGroup(MenuAdapter.CHARTS_MENU_IDX);
			mMenuDrawer.setItemChecked(groupPosition, true);
			setTitle(mDrawerMenuItems[groupPosition]);
			if (groupPosition != MenuAdapter.CHARTS_MENU_IDX) {
				mDrawerLayout.closeDrawer(mMenuDrawer);
			}

			lastSelectedGroupPosition = groupPosition;
			return true;
		}

		private void setFragment(Bundle args, Fragment fragment) {
			// update the main content by replacing fragments
			fragment.setArguments(args);

			FragmentManager fragmentManager = getFragmentManager();
//			fragmentManager.beginTransaction()
//					.replace(R.id.content_frame, fragment).commit();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}	
}
