package com.shico.mobilestats.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StatsDBHelper extends SQLiteOpenHelper {

	public final static String DB_NAME = "stats.db";
	public final static int DB_VERSION = 1;
	
	public StatsDBHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		ChartDataCmdTable.onCreate(db);
		ChartDataRecordsTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ChartDataCmdTable.onUpgrade(db, oldVersion, newVersion);
		ChartDataRecordsTable.onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON;");	
		}
	}
}
