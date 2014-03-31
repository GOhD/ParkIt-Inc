package com.google.gwt.ParkIt.shared;

import java.util.Date;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable="true")
@Inheritance(customStrategy = "complete-table")
public class UserMapEntry extends MapEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String CheckInType = "Check In";

	@Persistent
	private UserEntry user;

	@Persistent
	private long dateTimestamp;
		
	@Persistent
	private String description;
	
	public UserMapEntry() {
		this.type = null;
		this.latLong = new LatLong();
		this.user = null;
		this.description = "";
		this.dateTimestamp = 0;
	}
	
	public UserMapEntry(LatLong latLong, String type) {
		this.type = type;
		this.latLong = latLong;
		this.description = "";
		this.dateTimestamp = new Date().getTime();
	}
	
	public UserMapEntry(LatLong latLong, String type, String description) {
		this.type = type;
		this.latLong = latLong;
		this.description = description;
		this.dateTimestamp = new Date().getTime();
	}
	
	public static UserMapEntry CheckInEntry(UserEntry userEntry, LatLong latLong) {
		UserMapEntry entry = new UserMapEntry(latLong, CheckInType);
		entry.user = userEntry;
		entry.latLong = latLong;
		return entry;
	}
	
	public UserEntry getUser() {
		return user;
	}

	public long getDateTimestamp() {
		return dateTimestamp;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setUser(UserEntry ue) {
		this.user = ue;
	}
	
	@Override
	public String toString() {
		return "{" + super.toString() + ", User: " + user + ", Date: " + new Date(dateTimestamp) + ", Description: " + description + "}";
	}
}
