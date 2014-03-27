package com.google.gwt.ParkIt.client;

import com.google.gwt.ParkIt.shared.GoogleLoginInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GoogleLoginServiceAsync {

	void login(String requestUri, AsyncCallback<GoogleLoginInfo> callback);

}
