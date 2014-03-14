package com.google.gwt.ParkIt.client;


import com.google.gwt.dom.client.Document;
import com.google.maps.gwt.client.LatLng;
 

public class ParkingMeter {
	//private int ID;
	private String PRICE;
	private String ADDREES;
	private LatLng COORDINATES;

	
	
	public ParkingMeter(String price, String address, LatLng coordinates){
		this.PRICE = price;
		this.ADDREES = address;
		this.COORDINATES = coordinates;
}
	
	
	
	
	public LatLng getLatLng(){
		return this.COORDINATES;
	}
	
	public String getPrice(){
		return this.PRICE;
	}
	
	
}
