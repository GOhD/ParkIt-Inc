package com.google.gwt.ParkIt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.maps.gwt.client.LatLng;

public class data {
	
	private static List<ParkingMeter>  data = new ArrayList<ParkingMeter>();
	
	private static ParkingMeter p1 = new ParkingMeter("5", "2550 Wesbrook Mall, Vancouver, BC V6T1Z1", LatLng.create(49.268970,-123.252348));
	private static ParkingMeter p2 = new ParkingMeter("FREE!", "2525 West Mall, Vancouver, BC V6T 2A1", LatLng.create(49.264425,-123.244259));
	
	public static List<ParkingMeter> result(){
		data.add(p1);
		data.add(p2);
		return data;
		
	};
	
	private void add(ParkingMeter parkingMeter){
		data.add(parkingMeter);
		}
	
	

}