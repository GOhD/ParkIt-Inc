package com.google.gwt.ParkIt.server;

import java.util.ArrayList;

import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MapEntry;

public final class DistanceFinder {

	static double EarthRadius = 6371; // In Kilometers

	// Based off of: http://www.codecodex.com/wiki/Calculate_Distance_Between_Two_Points_on_a_Globe#Java
	/*
	 * Returns the distance between to coordinates in kilometers.
	 */
	public static double distanceBetweenPoints(LatLong P1, LatLong P2) {
		double lat1 = Math.toRadians(P1.getLat());
		double lon1 = Math.toRadians(P1.getLong());
		double lat2 = Math.toRadians(P2.getLat());
		double lon2 = Math.toRadians(P2.getLong());	

		double diffLat = lat2-lat1;
		double diffLon = lon2-lon1;

		double a = Math.sin(diffLat/2) * Math.sin(diffLat/2) +
				Math.cos(lat1) * Math.cos(lat2) *
				Math.sin(diffLon/2) * Math.sin(diffLon/2);

		double c = 2 * Math.asin(Math.sqrt(a));		

		return c*EarthRadius;
	}
	
	public static ArrayList<? extends MapEntry> pointsWithinRadius(double searchRadius, LatLong currentLocation, ArrayList<? extends MapEntry> listMapEntry) {
		if (currentLocation == null) {
			System.out.println("Current Location is Null");
			return listMapEntry;
		}
		
		ArrayList<MapEntry> result = new ArrayList<MapEntry>();
		
		for (MapEntry entry: listMapEntry) {
			if (distanceBetweenPoints(currentLocation, entry.getLatLong()) < searchRadius) {
				result.add(entry);
			}
		}
		return result;
	}
	
}
