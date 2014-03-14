package com.google.gwt.ParkIt.server;

import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;

import com.google.gwt.ParkIt.shared.MeterEntry;

public class KMLDataRetriever {

	private static final KMLDataRetriever DRSingleton = new KMLDataRetriever();

	protected KMLDataRetriever() {}

	public static KMLDataRetriever getInstance() {
		return DRSingleton;
	}

	URL parkingMeterDataUrl;
	Collection<MeterEntry> meterData; 

	public Collection<MeterEntry> retrieveMapData(){
		Collection<MeterEntry> result = new ArrayList<MeterEntry>();
		return result;
	}

	private Collection<MeterEntry> retrieveMeterData() {

		System.out.println("Retrieving Meter Data");

		try{
			parkingMeterDataUrl = new URL("ftp://webftp.vancouver.ca/OpenData/csv/parks.csv");
			URLConnection connection = parkingMeterDataUrl.openConnection();
			StringWriter writer = new StringWriter();
			IOUtils.copy(connection.getInputStream(), writer);
			String meterString = writer.toString();
			meterData = parseData(meterString, ',');
		}
		catch (Exception e) {
			System.out.println("Meter Data Retrieval Failed");
			e.printStackTrace();
		}

		return meterData;
	}

	private Collection<MeterEntry> parseData(String meterString, char c) {
		// TODO Auto-generated method stub
		return null;
	}

}
