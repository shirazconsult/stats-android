package com.shico.mobilestats.event;

public interface ChartEvent {
	public final static String DATA_LOAD_STATUS = "load.status";  // 0 for fail and 1 for sucess
	public final static String STATS_EVENT_DATA = "stats.event.data";
//	public final static String GROUP_VIEW_DATA = "group.view.data";
	public final static String DATA_LOAD_OPTIONS = "load.options";
	public final static String DATA_LOADER_ID = "loader.id";
	
	public final static int FAILURE = 0;
	public final static int SUCCESS = 1;
}
