package com.google.gwt.ParkIt.shared;

import java.io.Serializable;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable="true")
@EmbeddedOnly
public class LatLong implements Serializable {
	private static final long serialVersionUID = 1L;

	@Persistent
	private double lat;
	
	@Persistent
	private double lon;	
	
	public LatLong(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public LatLong() {
		this.lat = 0.0;
		this.lon = 0.0;
	}

	public double getLat() {
		return lat;
	}

	public double getLong() {
		return lon;
	}	
	
	public String toString() {
		return "LatLong {Lat: " + lat + ", Long: " + lon + "}";
	}
}
