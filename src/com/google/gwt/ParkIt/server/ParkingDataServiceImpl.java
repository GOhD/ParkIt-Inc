package com.google.gwt.ParkIt.server;

import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.ParkIt.client.ParkingDataService;
import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MeterEntry;

public class ParkingDataServiceImpl extends RemoteServiceServlet implements ParkingDataService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private void storeMeterEntry(MeterEntry meterentry) {
		PersistenceManager pm = getPersistenceManager();
		try {
			String name = meterentry.getName();
			LatLong latlong = meterentry.getLatLong();
			Double rate = meterentry.getRate();
			MeterEntry me = new MeterEntry(name, latlong, rate);
			pm.makePersistent(me);
		} finally {
			pm.close();
		}
	}
	
	public Collection<MeterEntry> fetchMeterEntries() {
		System.out.println("Called fetchMeterEntries");
		Collection<MeterEntry> mes = ParkingDataRetriever.getInstance().retrieveMeterData();
		System.out.println("Fetched data:" + mes);
		
		for (MeterEntry me : mes){
			storeMeterEntry(me);
		}
		return mes;
	}

	
	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}



}
