package com.google.gwt.ParkIt.client;

import java.util.Collection;

import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MapEntry;
import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.ParkIt.shared.UserMapEntry;

public final class JSMapService {
	
	private static native void displayMapEntry(String key, double lat, double lng, String type, String userName, String desc) /*-{
		$wnd.displayMapEntry(key,lat,lng,type,userName,desc);
	}-*/;
	
	public static void displayMapEntries(Collection<? extends MapEntry> entries) {
		for (MapEntry entry : entries) {
			LatLong latLng = entry.getLatLong();
			UserEntry user = null;
			String description = null;
			if (entry instanceof UserMapEntry) {
				user = ((UserMapEntry)entry).getUser();
				description = ((UserMapEntry)entry).getDescription();
			}
			String userName = (user != null ? user.getName() : null);
			displayMapEntry(entry.getKey(), latLng.getLat(), latLng.getLong(), entry.getType(), userName, description);
		}
	}
}
