package com.google.gwt.ParkIt.server;

import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.FetchGroup;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.ParkIt.client.UserDataService;
import com.google.gwt.ParkIt.shared.GoogleLoginInfo;
import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.ParkIt.shared.UserMapEntry;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserDataServiceImpl extends RemoteServiceServlet implements UserDataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	@Override
	public UserEntry getUser(GoogleLoginInfo gli) {
		PersistenceManager pm = getPersistenceManager();
		pm.getFetchPlan().setGroup(FetchGroup.ALL);
		UserEntry ue;
		try {
			ue = pm.getObjectById(UserEntry.class, gli.getEmailAddress());
			ue = pm.detachCopy(ue);
		} catch (JDOObjectNotFoundException e) {
			ue = createUser(gli);
		} finally {
			pm.close();
		}
		return ue;
	}
	
	private UserEntry createUser(GoogleLoginInfo gli) {
		PersistenceManager pm = getPersistenceManager();
		UserEntry ue = new UserEntry(gli);
		try {
			pm.makePersistent(ue);
			ue = pm.detachCopy(ue);
		} finally {
			pm.close();
		}
		return ue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<UserEntry> retrieveAllUsers() {
		PersistenceManager pm = getPersistenceManager();
		pm.getFetchPlan().setGroup(FetchGroup.ALL);
		Query q = pm.newQuery(UserEntry.class);
		ArrayList<UserEntry> userEntries;
		try {
			Collection<UserEntry> results = (Collection<UserEntry>) q.execute();
			userEntries = new ArrayList<UserEntry>(results);
			userEntries = (ArrayList<UserEntry>) pm.detachCopyAll(userEntries);
		} finally {
			q.closeAll();
			pm.close();
		}
		return userEntries;
	}
	
	public void userCheckIn(UserEntry user, LatLong currentLocation) {
		PersistenceManager pm = PMF.getPersistenceManager();
		pm.getFetchPlan().setGroup(FetchGroup.ALL);
		try {
			UserEntry ue = pm.getObjectById(UserEntry.class, user.getEmail());
			UserMapEntry ume = UserMapEntry.CheckInEntry(ue, currentLocation);
			ue.addCheckIn(ume);
		} finally {
			pm.close();
		}
	}
	
	public void setTwitterTokens(UserEntry user, String token, String tokenSecret) {
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			UserEntry ue = pm.getObjectById(UserEntry.class, user.getEmail());
			ue.setTwitterAccessToken(token);
			ue.setTwitterAccessTokenSecret(tokenSecret);
		} finally {
			pm.close();
		}
	}
	

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	
}
