package com.google.gwt.ParkIt.shared;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true")
public class MapEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String parkingMeter = "ParkingMeter"; 

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String key; 
	
	@Persistent
	protected String type;

	//@Persistent
	//protected String name;
	
	//@Persistent
	//protected String description;
	
	@Persistent
	protected LatLong latLong;
	

	public MapEntry() {
		this.type = "";
		//this.name = "";
		//this.description = "";
		this.latLong = new LatLong(0, 0);
	}

	public MapEntry(String type, LatLong latLong) {
		this.type = type;
		//this.name = name;	
		//this.description = description;
		this.latLong = latLong;	
	}
	
	// Getter Methods
	public String getKey() {
		return key;
	}
	
	public String getType() {
		return type;
	}
	
	//public String getName() {
	//	return name;
	//}
	
	//public String getDescription() {
	//	return description;
	//}

	public LatLong getLatLong() {
		return latLong;
	}
	
	public String toString() {
		return this.getClass().getName() + " {" + latLong + ", Type: " + type + ", " +"}";
	}


}
