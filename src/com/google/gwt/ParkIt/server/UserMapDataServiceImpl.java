package com.google.gwt.ParkIt.server;

import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.FetchGroup;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.ParkIt.client.UserMapDataService;
import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.ParkIt.shared.UserMapEntry;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserMapDataServiceImpl extends RemoteServiceServlet implements UserMapDataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public void storeUserMapEntry(UserMapEntry userMapEntry, UserEntry user) {
		PersistenceManager pm = getPersistenceManager();
		try {
			UserEntry ue = pm.getObjectById(UserEntry.class, user.getEmail());
			userMapEntry.setUser(ue);
			ue.addMapEntry(userMapEntry);
			pm.makePersistent(userMapEntry);
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<UserMapEntry> getUserMapEntries() {
		PersistenceManager pm = getPersistenceManager();
		pm.getFetchPlan().setGroup(FetchGroup.ALL);
		Query q = pm.newQuery(UserMapEntry.class);
		Collection<UserMapEntry> results;
		ArrayList<UserMapEntry> userMapEntries;
		try {
			results = (Collection<UserMapEntry>) q.execute();
			userMapEntries = new ArrayList<UserMapEntry>(results);
			userMapEntries = (ArrayList<UserMapEntry>) pm.detachCopyAll(userMapEntries);
		}
		finally {
			q.closeAll();
			pm.close();
		}
		return userMapEntries;
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}
	
	


}
