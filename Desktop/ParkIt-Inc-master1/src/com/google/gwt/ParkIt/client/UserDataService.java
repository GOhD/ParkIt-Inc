package com.google.gwt.ParkIt.client;


import java.util.Collection;

import com.google.gwt.ParkIt.shared.GoogleLoginInfo;
import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("UserDataService")
public interface UserDataService extends RemoteService {

	public UserEntry getUser(GoogleLoginInfo gli);
	
	public Collection<UserEntry> retrieveAllUsers();
	
	public void userCheckIn(UserEntry user, LatLong currentLocation);
	
}
