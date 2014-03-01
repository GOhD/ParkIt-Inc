package com.google.gwt.ParkIt.client;

public class LoginInfo {
	private Boolean googleLoginStatus;
	private String googleLoginUrl;
	private String googleLogoutUrl;

	public String getLoginUrl() {
		return googleLoginUrl;
	}
	public String getLogoutUrl() {
		return googleLogoutUrl;
	}
	public Boolean isLoggedIn() {
		return googleLoginStatus;
	}
	public void setGoogleLoginUrl(String _googleLoginUrl) {
		googleLoginUrl = _googleLoginUrl;
	}
	public void setGoogleLogoutUrl(String _googleLogoutUrl) {
		googleLogoutUrl = _googleLogoutUrl;
	}
	public void setGoogleLoginStatus(Boolean _googleLoginStatus) {
		googleLoginStatus = _googleLoginStatus;
	}
}