package com.shico.mobilestats.loaders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shico.mobilestats.event.ChartEvent;

public abstract class ChartDataLoader {
	private static final String CHART_CACHE = "chart_cache";
	private static final String CHART_DATA = "chart_data";
	
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

	private static final String REST_PATH = "/statistics/rest/stats";
	private static AsyncHttpClient client = new AsyncHttpClient();
	protected static JSONArray topViewColumns;
	protected Context context;
	private String baseUrl;
	private String host;
	private int port;
	
	protected JSONObject currentDataTable;

	public ChartDataLoader(Context context) throws JSONException {
		this.context = context;
		if(topViewColumns == null){
			topViewColumns = buildTopViewColumns();
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		host = prefs.getString("host", "localhost");
		port = Integer.parseInt(prefs.getString("port", "9118"));
	}
	
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(CHART_CACHE, (Serializable)getTemporaryCache());
	}
	public void onRestoreInstanceState(Bundle inState){
		setTemporaryCache((ConcurrentMap<String, JSONObject>) inState.get(CHART_CACHE));
	}
	private static JSONObject newColumn(String name, String type) throws JSONException{
		JSONObject col = new JSONObject();
		col.put("id", name);
		col.put("label", name);
		col.put("type", type);
		
		return col;
	}
		
//	public void getTopView(String restCmd, String from, String to, String options){
//		String url = new StringBuilder(getBaseUrl()).
//				append(restCmd).
//				append("/").append(from).
//				append("/").append(to).
//				append("/").append(options).
//				toString();
//		
//		JSONObject cached = getTemporaryCache().get(url);
//		if(cached == null){
//			loadChartTopViewData(url);
//		}else{
//			setCurrentDataTable(getTemporaryCache().get(url));
//			sendBroadcast(ChartEvent.SUCCESS);
//		}
//	}

	public void getTopViewInBatch(String restCmd, String from, String to, String options){
		String url = new StringBuilder(getBaseUrl()).
				append(restCmd).
				append("/").append(from).
				append("/").append(to).
				append("/").append(options).
				toString();
		
		JSONObject cached = getTemporaryCache().get(url);
		if(cached == null){
			Log.d("ChartDataLoader", "Loading data from server: "+url);
			loadChartTopViewDataInBatch(url);
		}else{
			currentDataTable = getTemporaryCache().get(url);
			Log.d("ChartDataLoader", "Loading data from cache: "+url);
			sendBroadcast(ChartEvent.SUCCESS);
		}
	}

	public void loadChartTopViewDataInBatch(final String url){
		client.get(url, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject res) {
				try {
					JSONObject newDataTable = newBatchDataTable();

					JSONArray rows = res.getJSONArray("result");
					int rownum = 0;
					for(int i=0; i< rows.length(); i++){
						JSONArray row = rows.getJSONObject(i).getJSONArray("rows");
						for (int j=0; j<row.length(); j++){			
							newDataTable.getJSONArray("rows").put(rownum++, row.getJSONObject(j).getJSONArray("result"));
						}
					}
					currentDataTable = newDataTable;
					getTemporaryCache().put(url, currentDataTable);
					temporaryRowCache = null;
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
	
//	public void loadChartTopViewData(final String url){
//		client.get(url, new JsonHttpResponseHandler(){
//			@Override
//			public void onSuccess(JSONObject res) {
//				try {
//					// empty the datatable rows
//					newDataTable().put("rows", new JSONArray());
//
//					JSONArray rows = res.getJSONArray("rows");					
//					for(int i=0; i< rows.length(); i++){
//						JSONArray row = rows.getJSONObject(i).getJSONArray("result");
//						newDataTable().getJSONArray("rows").put(row);
//					}
//					getTemporaryCache().put(url, newDataTable());
//					temporaryRowCache = null;
//				} catch (JSONException e) {
//					sendBroadcast(ChartEvent.FAILURE);
//					return;
//				}
//				sendBroadcast(ChartEvent.SUCCESS);
//			}
//
//			@Override
//			public void onFailure(Throwable t, JSONObject arg1) {
//				Log.e("ChartDataLoader", "Failed to retrieve data for "+getIntentAction()+". "+(t != null ? t.getMessage() : ""));
//				sendBroadcast(ChartEvent.FAILURE);
//			}
//		});
//	}
		
	private String getIntentAction() {
		return ChartEvent.STATS_EVENT_DATA;
	}

	private void sendBroadcast(int status){
		Intent intent = new Intent(getIntentAction());
		intent.putExtra(ChartEvent.DATA_LOAD_STATUS, status);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
	
	public List<List<Object>> getDataRows() throws JSONException{
		if(temporaryRowCache == null){
			temporaryRowCache = new ArrayList<List<Object>>();
			
			JSONArray rows = currentDataTable.getJSONArray("rows");
			for(int i=0; i<rows.length(); i++){
				List<Object> cacheRow = new ArrayList<Object>();
				JSONArray row = rows.getJSONArray(i);
				cacheRow.add(row.getString(nameIdx));
				cacheRow.add(row.getString(timeIdx));
				cacheRow.add(row.getInt(viewersIdx));
				cacheRow.add(row.getLong(durationIdx));
				
				temporaryRowCache.add(cacheRow);
			}			
		}
		return temporaryRowCache;
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

	private final String getBaseUrl() {
		if(baseUrl == null){
			baseUrl = new StringBuilder("http://").append(host).append(":").append(port).append(REST_PATH).toString();
		}
		return baseUrl;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}	
	
	private JSONObject newBatchDataTable() throws JSONException {
		JSONObject rawBatchDataTable = new JSONObject();
		rawBatchDataTable.put("cols", topViewColumns);
		rawBatchDataTable.put("rows", new JSONArray());
		return rawBatchDataTable;
	}

	private ConcurrentMap<String, JSONObject> temporaryCache;
	List<List<Object>> temporaryRowCache = null;
	
	public void setTemporaryCache(ConcurrentMap<String, JSONObject> temporaryCache){
		this.temporaryCache = temporaryCache;
	}
	
	public Map<String, JSONObject> getTemporaryCache(){
		if(temporaryCache == null){
			temporaryCache = new ConcurrentHashMap<String, JSONObject>();
		}
		return temporaryCache;
	}
}
