package com.shico.mobilestats.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChartDataCmdTable {
	  public static final String TABLE_CHART_DATA = "chart_data_cmd";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_PATH = "path";
	  public static final String COLUMN_FROM = "from";
	  public static final String COLUMN_TO = "to";
	  public static final String COLUMN_OPTIONS = "options";
	  private static final String TABLE_CREATE = "create table " 
	      + TABLE_CHART_DATA
	      + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_PATH + " text not null, " 
	      + COLUMN_FROM + " text not null," 
	      + COLUMN_TO + " text not null,"
	      + COLUMN_OPTIONS + " text not null" 
	      + ");";

	  public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(TABLE_CREATE);
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
	      int newVersion) {
	    Log.w(ChartDataCmdTable.class.getName(), "Upgrading database from version "
	        + oldVersion + " to " + newVersion
	        + ", which will destroy all old data");
	    database.execSQL("DROP TABLE IF EXISTS " + TABLE_CHART_DATA);
	    onCreate(database);
	  }
}
