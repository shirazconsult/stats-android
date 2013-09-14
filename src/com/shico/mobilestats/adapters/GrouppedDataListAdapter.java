package com.shico.mobilestats.adapters;

import java.text.NumberFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shico.mobilestats.R;

public class GrouppedDataListAdapter extends ArrayAdapter<List<Object>> {
	private static final NumberFormat numberFormatter = NumberFormat.getInstance();
	
	private Context context;
	private List<List<Object>> data;
	
	public GrouppedDataListAdapter(Context context,	List<List<Object>> rows) {
		super(context, R.layout.chart_groupped_data_list, rows);
		this.context = context;
		this.data = rows;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.chart_groupped_data_list, parent, false);
		TextView evType = (TextView)rowView.findViewById(R.id.event_name);
		TextView evTime = (TextView)rowView.findViewById(R.id.event_time);
		TextView viewers = (TextView)rowView.findViewById(R.id.viewers);
		TextView duration = (TextView)rowView.findViewById(R.id.duration);
		
		evType.setText((String)data.get(position).get(0));
		evTime.setText((String)data.get(position).get(1));
		float numOfViewers = ((Integer)data.get(position).get(2))/1000; 
		viewers.setText(numOfViewers+" thousands viewers");
		long hours = ((Long)data.get(position).get(3))/3600000;
		duration.setText(numberFormatter.format(hours)+" hours of total time");
		
		return rowView;
	}
}
