package com.google.gwt.ParkIt.client;

import java.util.Collection;

import com.google.gwt.ParkIt.shared.GoogleLoginInfo;
import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserDataServiceAsync {

	void getUser(GoogleLoginInfo gli, AsyncCallback<UserEntry> callback);

	void userCheckIn(UserEntry user, LatLong currentLocation, AsyncCallback<Void> callback);

	void retrieveAllUsers(AsyncCallback<Collection<UserEntry>> callback);

}
