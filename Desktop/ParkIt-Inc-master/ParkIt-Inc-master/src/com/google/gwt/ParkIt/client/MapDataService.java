package com.google.gwt.ParkIt.client;

import java.util.Collection;
import java.util.List;

import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MapEntry;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("MapDataService")
public interface MapDataService extends RemoteService {

	/**
	 * Queries the server for the most up to date MapEntries from
	 * third-party providers.
	 * 
	 * @return A collection of the most up to date MapEntries
	 */
	Collection<MapEntry> retrieveMapEntries(LatLong currentLocation, double searchRadius);
	
	/**
	 * 
	 * @param type The type of entry (ParkingMeter) to retrieve. 
	 * @return A collection of map entries of a specific type.
	 */
	Collection<MapEntry> getMapEntriesOfType(LatLong currentLocation, double searchRadius, List<String> types, boolean retrieveUserEntries);
		
}
