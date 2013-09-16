package com.shico.mobilestats.event;

public interface ChartEvent {
	public final static String DATA_LOAD_STATUS = "_load_status";  // 0 for fail and 1 for sucess
	public final static String STATS_EVENT_DATA = "_stats_event_data";

	public final static int FAILURE = 0;
	public final static int SUCCESS = 1;
}
