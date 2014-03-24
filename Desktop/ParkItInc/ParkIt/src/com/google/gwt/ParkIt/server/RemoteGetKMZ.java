package com.google.gwt.ParkIt.server;

import java.net.MalformedURLException; 
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.zip.*;

import org.apache.commons.io.IOUtils;

// Data Vancouver KML file URL: 
// http://data.vancouver.ca/download/kml/parking_meter_rates_and_time_limits.kmz

// Reference https://developers.google.com/appengine/docs/java/urlfetch/usingjavanet

public class RemoteGetKMZ{  

	private static final int maxlength = 10000000;

	public String getFile(){  

		try {
			URL url = new URL("http://data.vancouver.ca/download/kml/parking_meter_rates_and_time_limits.kmz");
			InputStreamReader is = new InputStreamReader(url.openStream());

			// Declare array to store file content
			byte[] bytes = IOUtils.toByteArray(is);


			// Unzip file with java.util.zip

			Inflater decompressor = new Inflater();
			decompressor.setInput(bytes, 0, bytes.length);

			byte[] result = new byte[maxlength];

			int resultLength;
			try {
				resultLength = decompressor.inflate(result);
			} catch (DataFormatException e) {
				// TODO Auto-generated catch block
				System.out.println("DataFormatException caught");
				e.printStackTrace();
				return "";
			}

			decompressor.end();

			// Decode the bytes into a String
			String outputString = new String(result, 0, resultLength, "utf-8");
			
			// TODO: Use outputString
			System.out.println(outputString);
			return outputString;

		} catch (MalformedURLException e) {
			// ...
		} catch (IOException e) {
			// ...
		}
		return "";
	}  


}  