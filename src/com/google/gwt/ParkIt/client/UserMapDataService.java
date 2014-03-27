package com.google.gwt.ParkIt.client;

import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.ParkIt.shared.UserMapEntry;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("UserMapDataService")
public interface UserMapDataService extends RemoteService {

	public void storeUserMapEntry(UserMapEntry userMapEntry, UserEntry user);

}
