package com.google.gwt.ParkIt.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true")
public class UserEntry implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent
	private String email;

	@Persistent
	private String twitterToken;
	
	@Persistent
	private String twitterTokenSecret;

	@Persistent
	private String name;
	
	@Persistent
	private String twitterAccessToken;
	
	@Persistent 
	private String twitterAccessTokenSecret;
	
	
	@Persistent(mappedBy = "user") 
	private Collection<UserMapEntry> userCheckInEntries;
	
	@Persistent(mappedBy = "user")
	private Collection<UserMapEntry> userMapEntries;
	
	public UserEntry() {
		email = null;
		name = null;
		userCheckInEntries = new LinkedList<UserMapEntry>();
		userMapEntries = new LinkedList<UserMapEntry>();
	}
	
	public UserEntry(GoogleLoginInfo gli) {
		email = gli.getEmailAddress();
		name = gli.getNickname();
		userCheckInEntries = new LinkedList<UserMapEntry>();
		userMapEntries = new LinkedList<UserMapEntry>();
	}

	public void addCheckIn(UserMapEntry checkInLoc) {
		userCheckInEntries.add(checkInLoc);
	}
	
	public void addMapEntry(UserMapEntry ume) {
		userMapEntries.add(ume);
	}
	
	public Collection<UserMapEntry> getUserCheckIns() {
		return userCheckInEntries;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getTwitterAccessToken() {
		return twitterAccessToken;
	}
	
	public void setTwitterAccessToken(String tac) {
		this.twitterAccessToken = tac;
	}
	
	public String getTwitterAccessTokenSecret() {
		return twitterAccessTokenSecret;
	}
	
	public void setTwitterAccessTokenSecret(String tac) {
		this.twitterAccessTokenSecret = tac;
	}
	
	@Override
	public String toString() {
		return "UserEntry [email=" + email + ", " +"]";
	}
}
