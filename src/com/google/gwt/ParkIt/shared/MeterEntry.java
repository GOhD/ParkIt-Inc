package com.google.gwt.ParkIt.shared;

import java.io.Serializable;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true") 
public class MeterEntry implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent
	private String name; 

	@Persistent
	@Embedded
	private LatLong latLong;
	
	@Persistent
	private Double price;
	
		
	public MeterEntry() {
		this.name = null;
		this.latLong = new LatLong(0, 0);
	}
	
	public MeterEntry(String name, LatLong latlong) {
		this.name = name;
		this.latLong = latlong;
	}
		
	public String getName() {
		return name;
	}
	
	public LatLong getLatLong() {
		return latLong;
	}
	
	public String toString() {
		return this.getClass().getName() + " {" + name + ", Type: " + latLong + ", " +"}";
	}
	
}
