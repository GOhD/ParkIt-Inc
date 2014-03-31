package com.google.gwt.ParkIt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.ParkIt.client.UserEntryTable.UserEntryTableDelegate;
import com.google.gwt.ParkIt.shared.GoogleLoginInfo;
import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MapEntry;
import com.google.gwt.ParkIt.shared.MeterEntry;
import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.ParkIt.shared.UserMapEntry;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ParkIt implements EntryPoint, UserEntryTableDelegate {
	private LatLong currentLocation;
	private GoogleLoginInfo gLoginInfo = null;
	private UserEntry currentUser;
	private Collection<MapEntry> mapEntries;
	private Collection<UserEntry> userEntries;
	private Collection<MeterEntry> meterEntries;
	private MapDataServiceAsync mapService = GWT.create(MapDataService.class);
	private ParkingDataServiceAsync meterService = GWT.create(ParkingDataService.class);
	private UserDataServiceAsync userService = GWT.create(UserDataService.class);
	private UserMapDataServiceAsync userMapService = GWT.create(UserMapDataService.class);
	private TypeToggler typeToggler;
	private DistanceSelector distanceSelector;
	private boolean isRetrievingUser, checkinOnUserRetrieval, hasLoadedMapEntries,hasLoadedUserEntries, canCheckin, isFirstLoad;
	private static final String[] mapEntryTypes = {MapEntry.parkingMeter};

	private LinkedList<String> contentToggle = new LinkedList<String>();

	/**
	 * This is the entry point method.
	 */

	public void onModuleLoad() {
		setupJSMethods(this);
		loginIfNecessary();
		//meterDataRetrieval();
	}

	public void loginIfNecessary() {
		if (gLoginInfo != null && gLoginInfo.isLoggedIn()) return;

		// Check login status using google login service.
		GoogleLoginServiceAsync gLoginService = GWT.create(GoogleLoginService.class);
		gLoginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<GoogleLoginInfo>() {
			public void onFailure(Throwable error) {
				error.printStackTrace();
			}

			public void onSuccess(GoogleLoginInfo result) {
				gLoginInfo = result;

				if(gLoginInfo.isLoggedIn()) {
					loadParkIt();
				} else {
					showLoginAlert(result.getLoginUrl());
				}
			}
		});
	}

	private static native void showLoginAlert(String loginUrl) /*-{
		$wnd.showWelcomeAlert(loginUrl);
	}-*/;

	private void loadParkIt() {
		if (mapService == null)
			mapService = GWT.create(MapDataService.class);

		//INSERT SEARCH BAR HERE
		ArrayList<String> types = new ArrayList<String>(Arrays.asList(mapEntryTypes));
		types.add("User");
		typeToggler = new TypeToggler(types, this);
		typeToggler.displayTypeToggler();
		int[] distances = {10, 20, 50, 100};
		distanceSelector = new DistanceSelector(distances, 1, this);
		if (gLoginInfo.getEmailAddress().equals("kyhee92@gmail.com") || gLoginInfo.getEmailAddress().equals("taffyluchia@gmail.com")) {
			distanceSelector.displayDistanceSelectorAdmin();
		}
		else {
			distanceSelector.displayDistanceSelector();	
		}	
		updateMapEntriesIfNecessary();
		loadUserEntries();
		//meterDataRetrieval();
	}

	private void updateMapEntriesIfNecessary() {
		if (hasLoadedMapEntries || gLoginInfo == null || !gLoginInfo.isLoggedIn() ||  currentLocation == null) return;
		hasLoadedMapEntries = true;

		System.out.println("Load Map Entries: Loading map entries");

		AsyncCallback<Collection<MapEntry>> callback = new AsyncCallback<Collection<MapEntry>>() {

			@Override
			public void onSuccess(Collection<MapEntry> result) {
				mapEntries = result;
				JSMapService.displayMapEntries(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Data Failed to Load");
			}
		};

		//Initialized Map Content
		double radius = (distanceSelector != null ? distanceSelector.getRadius() : 20.0);
		List <String> types = (typeToggler != null ? typeToggler.getSelectedTypes() : Arrays.asList(mapEntryTypes));

		if (isFirstLoad) {
			mapService.retrieveMapEntries(currentLocation, radius, callback);
			isFirstLoad = true;
		}
		else {
			boolean userEntries = types.contains("User");
			types.remove("User");
			mapService.getMapEntriesOfType(currentLocation, radius, types, userEntries, callback);
		}

		retrieveCurrentUser();
		if (currentLocation != null)
			checkIn();
	}

	private void loadUserEntries() {
		if (hasLoadedUserEntries || gLoginInfo == null || !gLoginInfo.isLoggedIn()) return;
		retrieveCurrentUser();
		hasLoadedUserEntries = true;

		System.out.println("Load User Entries: Loading user entries");

		final UserEntryTableDelegate delegate = this;
		AsyncCallback<Collection<UserEntry>> callback = new AsyncCallback<Collection<UserEntry>>() {

			@Override
			public void onSuccess(final Collection<UserEntry> result) {
				userEntries = result;
				UserEntryTable.displayUserEntryTable(result, currentUser, delegate);
				displayUserEntryUpdateButton();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("User data Failed to Load");
			}
		};

		userService.retrieveAllUsers(callback);
	}

	private void displayUserEntryUpdateButton() {
		// Add toggle button to the root panel.
		VerticalPanel panel = new VerticalPanel();
		panel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		panel.setSpacing(5);

		RootPanel.get("table").add(panel);	
	}

	public void setCurrentLocation(double lat, double lng) {
		currentLocation = new LatLong(lat, lng);
		canCheckin = true;
		if (gLoginInfo != null && gLoginInfo.isLoggedIn())
			checkIn();
		updateMapEntriesIfNecessary();
	}

	public void noteLocationUnavailable() {
		// default location
		canCheckin = false;
		currentLocation = new LatLong(49.2505, -123.1119);
		updateMapEntriesIfNecessary();
	}

	private void retrieveCurrentUser() {
		if (isRetrievingUser || gLoginInfo == null || !gLoginInfo.isLoggedIn()) return;

		isRetrievingUser = true;
		userService.getUser(gLoginInfo, new AsyncCallback<UserEntry>() {
			@Override
			public void onSuccess(UserEntry result) {
				currentUser = result;
				isRetrievingUser = false;
				if (checkinOnUserRetrieval) {
					checkInAsUser(result);
					checkinOnUserRetrieval = false;
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	private void checkInAsUser(UserEntry user) {
		userService.userCheckIn(user, currentLocation, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Checkin complete!");
			}

		});
	}

	private void checkIn() {
		// only check in if we have a valid location and login
		if (!canCheckin || currentLocation == null || gLoginInfo == null) return;

		if (currentUser != null) {
			checkInAsUser(currentUser);
			return;
		}

		checkinOnUserRetrieval = true;
		retrieveCurrentUser();
	}

	public String getCurrentUserName() {
		return (currentUser != null ? currentUser.getName() : null);
	}

	public void createUserMapEntry(double lat, double lng, String type, String description) {
		if (type == null || currentUser == null) return;

		LatLong latLng = new LatLong(lat, lng);
		UserMapEntry mapEntry = new UserMapEntry(latLng, type, description);
		userMapService.storeUserMapEntry(mapEntry, currentUser, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("success!");
			}
		});
	}

	public void logout() {
		if (gLoginInfo.isLoggedIn() && gLoginInfo.getLogoutUrl() != null) {
			Window.Location.assign(gLoginInfo.getLogoutUrl());
		}
	}


	private static native void setupJSMethods(ParkIt instance) /*-{
		$wnd.setCurrentLocation = $entry(function(lat, lng) { 
			instance.@com.google.gwt.ParkIt.client.ParkIt::setCurrentLocation(DD)(lat, lng); 
		});
		$wnd.noteLocationUnavailable = $entry(function() {
			instance.@com.google.gwt.ParkIt.client.ParkIt::noteLocationUnavailable()();
		});
		$wnd.getCurrentUserName = $entry(function() {
			return instance.@com.google.gwt.ParkIt.client.ParkIt::getCurrentUserName()();
		});
		$wnd.createUserMapEntry = $entry(function(lat, lng, type, desc) {
			instance.@com.google.gwt.ParkIt.client.ParkIt::createUserMapEntry(DDLjava/lang/String;Ljava/lang/String;)(lat, lng, type, desc);
		});
		$wnd.logout = $entry(function() {
			instance.@com.google.gwt.ParkIt.client.ParkIt::logout()();
		});
		$wnd.loginIfNecessary = $entry(function() {
			instance.@com.google.gwt.ParkIt.client.ParkIt::loginIfNecessary()();
		});

	}-*/;

	private static native void displayHints(JsArrayString hints) /*-{
		$wnd.setHints(hints);
	}-*/;

	private static native void clearAllMapEntries() /*-{
		$wnd.removeAllEntries();
	}-*/;


	public void updateMapEntries() {
		clearAllMapEntries();
		hasLoadedMapEntries = false;
		updateMapEntriesIfNecessary();
	}

	/*@SuppressWarnings("unchecked")
	public void meterDataRetrieval() {
		// (1) Create the client proxy. Note that although you are creating the
		// service interface proper, you cast the result to the asynchronous
		// version of the interface. The cast is always safe because the
		// generated proxy implements the asynchronous interface automatically.
		//
		ParkingDataServiceAsync parkingDataService = (ParkingDataServiceAsync) GWT.create(ParkingDataService.class);

		// (2) Create an asynchronous callback to handle the result.
		//
		AsyncCallback<Collection<MeterEntry>> callback = new AsyncCallback<Collection<MeterEntry>>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Collection<MeterEntry> result) {
				System.out.println("Meter Data Update: Success!");
				System.out.println(result);
			}
			
		};
		
		System.out.println("Fetching data!");

		// (3) Make the call. Control flow will continue immediately and later
		// 'callback' will be invoked when the RPC completes.
		//
		parkingDataService.fetchMeterEntries(callback);
	}*/



}
