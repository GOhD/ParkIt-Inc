package com.google.gwt.ParkIt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.ParkIt.shared.LoginInfo;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
	
	String getUserEmail(String token);	

	LoginInfo login(String requestUri);	

	LoginInfo loginDetails(String token);
}

