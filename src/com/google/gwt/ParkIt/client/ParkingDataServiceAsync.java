package com.google.gwt.ParkIt.client;

import java.util.Collection;

import com.google.gwt.ParkIt.shared.MeterEntry;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ParkingDataServiceAsync {

	void fetchMeterEntries(AsyncCallback<Collection<MeterEntry>> callback);

}
