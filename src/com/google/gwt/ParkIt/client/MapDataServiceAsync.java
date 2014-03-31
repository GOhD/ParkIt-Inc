package com.google.gwt.ParkIt.client;

import java.util.Collection;
import java.util.List;

import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MapEntry;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MapDataServiceAsync {

	void retrieveMapEntries(LatLong currentLocation, double searchRadius,
			AsyncCallback<Collection<MapEntry>> callback);

	void getMapEntriesOfType(LatLong currentLocation, double searchRadius,
			List<String> types, boolean retrieveUserEntries,
			AsyncCallback<Collection<MapEntry>> callback);


}
