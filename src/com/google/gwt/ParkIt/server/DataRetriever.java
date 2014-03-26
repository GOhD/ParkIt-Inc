package com.google.gwt.ParkIt.server;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.PersistenceManager;

import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MapEntry;
import com.google.gwt.ParkIt.shared.PMF;

public class DataRetriever {
	
	private static final DataRetriever DRSingleton = new DataRetriever();
	
	protected DataRetriever() {}
	
	public static DataRetriever getInstance() {
		return DRSingleton;
	}
	
	volatile boolean meterUpToDate = false;
	long lastMeterDataUpdate;
	
	URL meterDataUrl; 
	Collection<MapEntry> meterData; 
	
	public Collection<MapEntry> retrieveMapData(){
		Collection<MapEntry> result = new ArrayList<MapEntry>();
		Collection<MapEntry> meter = retrieveMeterData();
		result.addAll(meter);
		return result;
	}
		
	private Collection<MapEntry> retrieveMeterData() {
		isTimestampRecent(lastMeterDataUpdate, meterUpToDate);
		if (!meterUpToDate) {
			System.out.println("Retrieving Meter Data and Updating Timestamp");
			
			try{
				meterDataUrl = new URL("https://dl.dropboxusercontent.com/u/5731404/dataVancouver.csv");
				URLConnection connection = meterDataUrl.openConnection();
				//InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
				StringWriter writer = new StringWriter();
				IOUtils.copy(connection.getInputStream(), writer);
				String meterString = writer.toString();
				meterData = parseData(meterString, ',', MapEntry.parkingMeter);
			}
			catch (Exception e) {
				System.out.println("Meter Retrieval Failed");
				e.printStackTrace();
			}
			lastMeterDataUpdate = System.currentTimeMillis();
			meterUpToDate = true;
		}
		return meterData;
	}

	public boolean isUpToDate(){
		return meterUpToDate;
	}

	/*
	 * Checks that the timestamp is less than 1 week old.
	 */
	private void isTimestampRecent(long lastUpdate, boolean flag) {
		flag = System.currentTimeMillis() - lastUpdate <= 604800000;	
	}

	protected Collection<MapEntry> parseData(String data, char separator, String type) {
		Collection<MapEntry> entries = new ArrayList<MapEntry>();

		try {
			// initialize indices to invalid numbers to make sure valid indices are found
			final int invalidIndex = Integer.MAX_VALUE;
			int latIndex = invalidIndex;
			int longIndex = invalidIndex;
			//int nameIndex = invalidIndex;
			//int descriptionIndex = invalidIndex;
			
			CSVReader reader = new CSVReader(new StringReader(data), separator);
			
			String[] header = reader.readNext();
			for (int i = 0; i < header.length; i++) {
				// find indices of keys we care about 
				String key = header[i];
				if (key.equalsIgnoreCase("latitude"))
					latIndex = i;
				else if (key.equalsIgnoreCase("longitude"))
					longIndex = i;
			}
			
			if (latIndex == invalidIndex || longIndex == invalidIndex) {
				reader.close();
				throw new IOException("Could not find latitude and longitude in given CSV file for type " + type);
			}
			
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine.length != header.length)
					continue; // skip line if it has a different # of columns from header (ie. for last line)
				
				double lat = Double.parseDouble(nextLine[latIndex]);
				double lon = Double.parseDouble(nextLine[longIndex]);
				MapEntry entry = new MapEntry(type, new LatLong(lat, lon));
				entries.add(entry);
			}
			
			reader.close();
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		catch (NumberFormatException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return entries;
	}
	
}
