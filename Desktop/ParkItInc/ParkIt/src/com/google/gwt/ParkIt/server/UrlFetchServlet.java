package com.google.gwt.ParkIt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

	// Data Vancouver KML file URL: 
	// http://data.vancouver.ca/download/kml/parking_meter_rates_and_time_limits.kmz
	// https://dl.dropboxusercontent.com/u/5731404/dataVancouver
	// ftp://webftp.vancouver.ca/OpenData/csv/parks.csv

@SuppressWarnings("serial")
public class UrlFetchServlet extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
			URL url = new URL("ftp://webftp.vancouver.ca/OpenData/csv/parks.csv");
			BufferedReader reader = new BufferedReader(new
					InputStreamReader(url.openStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				resp.getOutputStream().println(line);
			}
			reader.close();
		} catch (MalformedURLException e) {
			resp.getOutputStream().println(e.getMessage());
		} catch (IOException e) {
			resp.getOutputStream().println(e.getMessage());
		}
	}
}