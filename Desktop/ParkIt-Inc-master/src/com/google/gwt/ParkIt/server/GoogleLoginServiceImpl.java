package com.google.gwt.ParkIt.server;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.ParkIt.client.GoogleLoginService;
import com.google.gwt.ParkIt.shared.GoogleLoginInfo;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GoogleLoginServiceImpl extends RemoteServiceServlet implements GoogleLoginService{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GoogleLoginInfo login(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		GoogleLoginInfo loginInfo = new GoogleLoginInfo();

		if (user != null) {
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
		} else {
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return loginInfo;
	}
	
}
