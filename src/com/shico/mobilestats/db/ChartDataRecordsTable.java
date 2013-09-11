package com.shico.mobilestats.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChartDataRecordsTable {
	  public static final String TABLE_CHART_DATA_RECORDS = "chart_data_recs";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_CMD_ID = "_cmd_id";
	  public static final String COLUMN_REC = "rec";
	  
	  private static final String TABLE_CREATE = "create table " 
	      + TABLE_CHART_DATA_RECORDS
	      + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_REC + " text not null, "
	      + COLUMN_CMD_ID + " integer NOT NULL,FOREIGN KEY ("
	      + COLUMN_CMD_ID +") REFERENCES "
	      + ChartDataCmdTable.TABLE_CHART_DATA +" ("+ChartDataCmdTable.COLUMN_ID+"));";

	  public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(TABLE_CREATE);
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
	      int newVersion) {
	    Log.w(ChartDataRecordsTable.class.getName(), "Upgrading database from version "
	        + oldVersion + " to " + newVersion
	        + ", which will destroy all old data");
	    database.execSQL("DROP TABLE IF EXISTS " + TABLE_CHART_DATA_RECORDS);
	    onCreate(database);
	  }
}
