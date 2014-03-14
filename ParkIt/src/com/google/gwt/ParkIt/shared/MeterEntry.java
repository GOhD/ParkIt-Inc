package com.google.gwt.ParkIt.shared;

import java.io.Serializable;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true")
public class MeterEntry implements Serializable {

	// Default serial version ID
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent
    private String id;
	
	@Persistent
	protected String name;

	@Persistent
	@Embedded
	protected LatLong latLong;
	
	@Persistent
	@Embedded
	protected String rate;
	
	@Persistent
	@Embedded
	protected String timeInEffect;


	public MeterEntry(String id, String name, LatLong latLong, String rate, String timeInEffect) {
		this.id = id;
		this.name = name;
		this.latLong = latLong;	
		this.rate = rate;
		this.timeInEffect = timeInEffect;
		
	}
	
	// Getter Methods
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public LatLong getLatLong() {
		return latLong;
	}

	public String getRate() {
		return rate;
	}

	public String getTimeInEffect() {
		return timeInEffect;
	}


}
