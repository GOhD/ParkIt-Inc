package com.google.gwt.ParkIt.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.FetchGroup;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.ParkIt.client.MapDataService;
import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MapEntry;
import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.ParkIt.shared.UserMapEntry;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class MapDataServiceImpl extends RemoteServiceServlet implements MapDataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	@SuppressWarnings("unchecked")
	@Override
	public Collection<MapEntry> retrieveMapEntries(LatLong currentLocation, double searchRadius){
		DataRetriever DR = DataRetriever.getInstance();
		
		// If data is up to data retrieve from application server and return it.
		boolean upToDate = DR.isUpToDate();
		
		if (upToDate) {
			System.out.println("Loading From Database");
			LinkedList<String> specifier = new LinkedList<String>();
			specifier.add("ParkingMeter");
			ArrayList<MapEntry> results = (ArrayList<MapEntry>) getMapEntriesOfType(currentLocation, searchRadius, specifier, true);
			return results;
		}
		// Otherwise make request for data, store the parsed response and return it.
		else {
 			System.out.println("Loading From External Services");
 			// Flush current contents.
 			deleteMapEntriesOfType(MapEntry.parkingMeter);
 	
 			//Pull content in.
 			Collection<MapEntry> mapEntries = DR.retrieveMapData();	
			ArrayList<MapEntry> results = (ArrayList<MapEntry>) putMapEntries(mapEntries);
 			results = (ArrayList<MapEntry>) DistanceFinder.pointsWithinRadius(searchRadius, currentLocation, results);
 			return results;

		}
	}

	public Collection<MapEntry> putMapEntries(Collection<MapEntry> mapEntries) {
		PersistenceManager pm = getPersistenceManager();
		Collection<MapEntry> results;
		try {
			pm.makePersistentAll(mapEntries);
			results = pm.detachCopyAll(mapEntries);
		} finally {
			pm.close();
		}
		return results;
	}


	@SuppressWarnings("unchecked")
	public Collection<MapEntry> getMapEntries() {
		PersistenceManager pm = getPersistenceManager();
		pm.getFetchPlan().setGroup(FetchGroup.ALL);
		Query q = pm.newQuery(MapEntry.class);
		ArrayList<MapEntry> mapEntries;		
		try {
			Collection<MapEntry> results = (Collection<MapEntry>) q.execute();
			mapEntries = new ArrayList<MapEntry>(results);
			mapEntries = (ArrayList<MapEntry>) pm.detachCopyAll(mapEntries);
		}
		finally {
			q.closeAll();
			pm.close();
		}
		
		return mapEntries;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<MapEntry> getMapEntriesOfType(LatLong currentLocation, double searchRadius, List<String> types, boolean retrieveUserEntries) {
		PersistenceManager pm = getPersistenceManager();
		pm.getFetchPlan().setGroup(FetchGroup.ALL);
		Query q = pm.newQuery(MapEntry.class, ":p.contains(type)");
		
		ArrayList<MapEntry> mapEntries;		
		try {
			Collection<MapEntry> queryResults = (Collection<MapEntry>) q.execute(types);
			mapEntries = new ArrayList<MapEntry>(queryResults);
			mapEntries = (ArrayList<MapEntry>) pm.detachCopyAll(mapEntries);
		}
		finally {
			q.closeAll();
		}
		
		if (retrieveUserEntries) {
			q = pm.newQuery(UserMapEntry.class, ":p.contains(type)");
			ArrayList<MapEntry> userMapEntries;
			try {
				Collection<MapEntry> queryResults = (Collection<MapEntry>) q.execute(types);
				userMapEntries = new ArrayList<MapEntry>(queryResults);
				userMapEntries = (ArrayList<MapEntry>) pm.detachCopyAll(userMapEntries);
				mapEntries.addAll(userMapEntries);
			}
			finally {
				q.closeAll();
			}	
		}
		pm.close();
		mapEntries = (ArrayList<MapEntry>) DistanceFinder.pointsWithinRadius(searchRadius, currentLocation, mapEntries);
		return mapEntries;
	}	
	
	public void deleteMapEntriesOfType(String type) {
		PersistenceManager pm = getPersistenceManager();
		Query q = pm.newQuery(MapEntry.class);
		q.setFilter("type == typeParam");
		q.declareParameters("String typeParam");
		q.deletePersistentAll(type);
		q.closeAll();
		pm.close();
	}

	
	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}
}
