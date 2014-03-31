package com.google.gwt.ParkIt.server;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MeterEntry;

public class ParkingDataRetriever {

	private static final ParkingDataRetriever PDRSingleton = new ParkingDataRetriever();
	
	protected ParkingDataRetriever() {}
	
	public static ParkingDataRetriever getInstance() {
		return PDRSingleton;
	}
	
	URL meterDataUrl; 
	Collection<MeterEntry> meterData; 	
	
	public Collection<MeterEntry> retrieveMeterData() {
			System.out.println("Retrieving Meter Data from URL");
			
			try{
				meterDataUrl = new URL("https://dl.dropboxusercontent.com/u/5731404/dataVancouver2.csv");
				URLConnection connection = meterDataUrl.openConnection();
				//InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
				StringWriter writer = new StringWriter();
				IOUtils.copy(connection.getInputStream(), writer);
				String meterString = writer.toString();
				meterData = parseData(meterString, ',');
			}
			catch (Exception e) {
				System.out.println("Meter Retrieval Failed");
				e.printStackTrace();
			}
			
		return meterData;
	}

	protected Collection<MeterEntry> parseData(String data, char separator) {
		//Collection<MeterEntry> entries = new ArrayList<MeterEntry>();
		ArrayList<MeterEntry> newMeterData = new ArrayList<MeterEntry>();
		try {
			// initialize indices to invalid numbers to make sure valid indices are found
			final int invalidIndex = Integer.MAX_VALUE;
			
			int nameIndex = invalidIndex;
			int latIndex = invalidIndex;
			int longIndex = invalidIndex;
			int commentIndex = invalidIndex;
			
			
			CSVReader reader = new CSVReader(new StringReader(data), separator);
			
			//CSV File has header row
			String[] header = reader.readNext();
			
			for (int i = 0; i < header.length; i++) {
				// find indices of keys we care about 
				String key = header[i];
				if (key.equalsIgnoreCase("latitude"))
					latIndex = i;
				else if (key.equalsIgnoreCase("longitude"))
					longIndex = i;
				else if (key.equalsIgnoreCase("name"))
					nameIndex = i;
				else if (key.equalsIgnoreCase("comment"))
					commentIndex = i;
			}
			
			if (latIndex == invalidIndex || longIndex == invalidIndex || nameIndex == invalidIndex) {
				reader.close();
				throw new IOException("Could not find LatLong and MeterName in given CSV file");
			}
			
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine.length != header.length)
					continue; // skip line if it has a different # of columns from header (ie. for last line)
				
				double lat = Double.parseDouble(nextLine[latIndex]);
				double lon = Double.parseDouble(nextLine[longIndex]);
				String name = nextLine[nameIndex];
				String comment = nextLine[commentIndex];
				int rateIndex = comment.lastIndexOf("$") + 1;
				String rateString = comment.substring(rateIndex, rateIndex + 4);
				double rate = Double.parseDouble(rateString);
				MeterEntry entry = new MeterEntry(name, new LatLong(lat, lon), rate);
				newMeterData.add(entry);
				
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
		
		return newMeterData;
	}
	
}
