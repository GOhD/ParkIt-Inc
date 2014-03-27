package com.google.gwt.ParkIt.client;

import com.google.gwt.ParkIt.shared.GoogleLoginInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface GoogleLoginService extends RemoteService {
	public GoogleLoginInfo login(String requestUri);
}
