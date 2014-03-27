package com.google.gwt.ParkIt.client;

import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.ParkIt.shared.UserMapEntry;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserMapDataServiceAsync {

	void storeUserMapEntry(UserMapEntry userMapEntry, UserEntry user,
			AsyncCallback<Void> callback);
	
}
