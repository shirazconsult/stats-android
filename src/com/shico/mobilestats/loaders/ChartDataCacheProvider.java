package com.shico.mobilestats.loaders;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.shico.mobilestats.db.StatsDBHelper;

public class ChartDataCacheProvider extends AsyncTask<String, Integer, Void>{
	private StatsDBHelper dbHelper;
	private Context context;
	
	public ChartDataCacheProvider(Context context){
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String restCmd = params[0];
		String options = params[1];
		
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(dbHelper == null){
			dbHelper = new StatsDBHelper(context);
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}
	
}
