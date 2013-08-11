package com.shico.mobilestats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class MenuAdapter extends BaseExpandableListAdapter {
	public final static int CHARTS_MENU_IDX = 0;
	public final static int SETTINGS_MENU_IDX = 1;
	public final static int HELP_MENU_IDX = 2;
	public final static int ABOUT_MENU_IDX = 3;

	private Activity context;
	private String[] menuItems;
	private String[] chartItems;
	
	public MenuAdapter(Activity context) {
		super();
		this.context = context;
		menuItems = context.getResources().getStringArray(R.array.menu_items);
		chartItems = context.getResources().getStringArray(R.array.menu_chart_items);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.drawer_menu_item, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.drawer_menu_item);
        item.setText((String) getGroup(groupPosition));
        return convertView;
    }

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if(getChildrenCount(groupPosition) == 0){
			return convertView;
		}
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.menu_chart_item, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.chart_menu_item);
        item.setText((String)getChild(groupPosition, childPosition));
        return convertView;
	}

	@Override
	public int getGroupCount() {
		return menuItems.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(groupPosition == CHARTS_MENU_IDX){
			return chartItems.length;
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return menuItems[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if(groupPosition == CHARTS_MENU_IDX){
			return chartItems[childPosition];
		}
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		if(groupPosition == CHARTS_MENU_IDX){
			return childPosition;					
		}
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
