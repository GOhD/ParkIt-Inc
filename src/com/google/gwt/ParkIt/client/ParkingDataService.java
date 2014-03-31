package com.google.gwt.ParkIt.client;

import java.util.Collection;

import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MeterEntry;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/*Client side stub for the RPC service*/

@RemoteServiceRelativePath("ParkingDataService")
public interface ParkingDataService extends RemoteService{
	
	public Collection<MeterEntry> fetchMeterEntries();

}
