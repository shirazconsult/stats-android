package com.shico.mobilestats.loaders;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shico.mobilestats.event.ChartEvent;

public abstract class ChartDataLoader {
	// data index constants
	public final static int typeIdx = 0; 
	public final static int nameIdx = 1; 
	public final static int titleIdx = 2;
	public final static int viewersIdx = 3;
	public final static int durationIdx = 4;
	public final static int fromIdx = 5;
	public final static int timeIdx = 5;
	public final static int toIdx = 6;
	public static String[] viewColumns = {"type", "name", "title", "viewers", "duration", "fromTS", "toTS"};
	public static String[] topViewColumnNames = {"type", "name", "title", "viewers", "duration", "time"};

	private static final String BASE_URL = "http://10.0.2.2:9119/statistics/rest/stats";
	private static AsyncHttpClient client = new AsyncHttpClient();
	protected static JSONArray topViewColumns;
	protected Context context;
	
	public ChartDataLoader(Context context) throws JSONException {
		this.context = context;
		if(topViewColumns == null){
			topViewColumns = buildTopViewColumns();
		}
	}
	
	private static JSONObject newColumn(String name, String type) throws JSONException{
		JSONObject col = new JSONObject();
		col.put("id", name);
		col.put("label", name);
		col.put("type", type);
		
		return col;
	}
	
	Map<String, JSONObject> temporaryCache = new HashMap<String, JSONObject>();
	
	public void getTopView(String restCmd, String from, String to, String options){
		String url = new StringBuilder(BASE_URL).
				append(restCmd).
				append("/").append(from).
				append("/").append(to).
				append("/").append(options).
				toString();
		
		JSONObject cached = temporaryCache.get(url);
		if(cached == null){
			loadChartTopViewData(url);
		}else{
			sendBroadcast(ChartEvent.SUCCESS);
		}
	}

	public void getTopViewInBatch(String restCmd, String from, String to, String options){
		String url = new StringBuilder(BASE_URL).
				append(restCmd).
				append("/").append(from).
				append("/").append(to).
				append("/").append(options).
				toString();
		
		JSONObject cached = temporaryCache.get(url);
		if(cached == null){
			loadChartTopViewDataInBatch(url);
		}else{
			sendBroadcast(ChartEvent.SUCCESS);
		}
	}

	public abstract JSONObject getDataTable() throws JSONException;
	protected abstract String getIntentAction();
		
	public void loadChartTopViewDataInBatch(final String url){
		client.get(url, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject res) {
				try {
					JSONArray rows = res.getJSONArray("result");
					int rownum = 0;
					for(int i=0; i< rows.length(); i++){
						JSONArray row = rows.getJSONObject(i).getJSONArray("rows");
						for (int j=0; j<row.length(); j++){			
							getDataTable().getJSONArray("rows").put(rownum++, row.getJSONObject(j).getJSONArray("result"));
						}
						temporaryCache.put(url, getDataTable());
					}
				} catch (JSONException e) {
					sendBroadcast(ChartEvent.FAILURE);
					return;
				}
				sendBroadcast(ChartEvent.SUCCESS);
			}

			@Override
			public void onFailure(Throwable t, JSONObject arg1) {
				Log.e("ChartDataLoader", "Failed to retrieve data for "+getIntentAction()+". "+(t != null ? t.getMessage() : ""));
				sendBroadcast(ChartEvent.FAILURE);
			}
		});		
	}
	
	public void loadChartTopViewData(final String url){
		client.get(url, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject res) {
				try {
					JSONArray rows = res.getJSONArray("rows");
					for(int i=0; i< rows.length(); i++){
						JSONArray row = rows.getJSONObject(i).getJSONArray("result");
						getDataTable().getJSONArray("rows").put(row);
					}
					temporaryCache.put(url, getDataTable());
				} catch (JSONException e) {
					sendBroadcast(ChartEvent.FAILURE);
					return;
				}
				sendBroadcast(ChartEvent.SUCCESS);
			}

			@Override
			public void onFailure(Throwable t, JSONObject arg1) {
				Log.e("ChartDataLoader", "Failed to retrieve data for "+getIntentAction()+". "+(t != null ? t.getMessage() : ""));
				sendBroadcast(ChartEvent.FAILURE);
			}
		});
	}
		
	private void sendBroadcast(int status){
		Intent intent = new Intent(getIntentAction());
		intent.putExtra(ChartEvent.DATA_LOAD_STATUS, status);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
	
	private String getLiveUsageUrl(String from, String to){
		return new StringBuilder(BASE_URL).
				append("/view/LiveUsage/").
				append(from).append("/").append(to).
				append("/viewers,top,10").
				toString();
	}
	
	protected static JSONArray buildTopViewColumns() throws JSONException{
		JSONArray cols = new JSONArray();
		cols.put(typeIdx, newColumn(topViewColumnNames[typeIdx], "string"));
		cols.put(nameIdx, newColumn(topViewColumnNames[nameIdx], "string"));
		cols.put(titleIdx, newColumn(topViewColumnNames[titleIdx], "string"));
		cols.put(viewersIdx, newColumn(topViewColumnNames[viewersIdx], "number"));
		cols.put(durationIdx, newColumn(topViewColumnNames[durationIdx], "number"));
		cols.put(timeIdx, newColumn(topViewColumnNames[timeIdx], "string"));
		
		return cols;
	}	
}
