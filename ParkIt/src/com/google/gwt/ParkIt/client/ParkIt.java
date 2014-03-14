package com.google.gwt.ParkIt.client;

import java.util.List;

import com.google.gwt.ParkIt.shared.FieldVerifier;
import com.google.gwt.ParkIt.shared.LoginInfo;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.InfoWindowOptions;
import com.google.maps.gwt.client.KmlFeatureData;
import com.google.maps.gwt.client.KmlLayer;
import com.google.maps.gwt.client.KmlLayerOptions;
import com.google.maps.gwt.client.KmlMouseEvent;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.LatLngBounds;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MouseEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ParkIt implements EntryPoint {
	
	private static final Auth AUTH = Auth.get();
	private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
	private static final String GOOGLE_CLIENT_ID = "930721009291-chr21vdp45demnnlrdmmucvven40qpum.apps.googleusercontent.com";
	private static final String PLUS_ME_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
	
	private final HorizontalPanel loginPanel = new HorizontalPanel();
	private final Anchor signInLink = new Anchor("");
	private final Image loginImage = new Image();
	private final TextBox nameField = new TextBox();
	
	GoogleMap map;
	private VerticalPanel vPanel = new VerticalPanel();
	private HorizontalPanel hPanel = new HorizontalPanel();
	private TextBox textBox = new TextBox();
	private Button button = new Button("Search!");
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	
	private void loadLogin(final LoginInfo loginInfo) {
		signInLink.setHref(loginInfo.getLoginUrl());
		signInLink.setText("Please, sign in with your Google Account");
		signInLink.setTitle("Sign in");
	}

	private void loadLogout(final LoginInfo loginInfo) {
		signInLink.setHref(loginInfo.getLogoutUrl());
		signInLink.setText(loginInfo.getName());
		signInLink.setText("Log out.");
		signInLink.setTitle("Sign out");
	}
	
	private void addGoogleAuthHelper() {
		final AuthRequest req = new AuthRequest(GOOGLE_AUTH_URL, GOOGLE_CLIENT_ID)
				.withScopes(PLUS_ME_SCOPE);
		AUTH.login(req, new Callback<String, Throwable>() {
			@Override
			public void onSuccess(final String token) {

				if (!token.isEmpty()) {
					greetingService.loginDetails(token, new AsyncCallback<LoginInfo>() {
						@Override
						public void onFailure(final Throwable caught) {
							GWT.log("loginDetails -> onFailure");
						}

						@Override
						public void onSuccess(final LoginInfo loginInfo) {
							signInLink.setText(loginInfo.getName());
							nameField.setText(loginInfo.getName());
							signInLink.setStyleName("login-area");
							loginImage.setUrl(loginInfo.getPictureUrl());
							loginImage.setVisible(false);
							loginPanel.add(loginImage);
							loginImage.addLoadHandler(new LoadHandler() {
								@Override
								public void onLoad(final LoadEvent event) {
									final int newWidth = 24;
									final com.google.gwt.dom.client.Element element = event
											.getRelativeElement();
									if (element.equals(loginImage.getElement())) {
										final int originalHeight = loginImage.getOffsetHeight();
										final int originalWidth = loginImage.getOffsetWidth();
										if (originalHeight > originalWidth) {
											loginImage.setHeight(newWidth + "px");
										} else {
											loginImage.setWidth(newWidth + "px");
										}
										loginImage.setVisible(true);
									}
								}
							});
						}
					});
				}
			}

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("Error -> loginDetails\n" + caught.getMessage());
			}
		});
	}

	public void onModuleLoad() {
		
		button.setEnabled(false);
		textBox.setEnabled(false);
		signInLink.getElement().setClassName("login-area");
		signInLink.setTitle("sign out");
		loginImage.getElement().setClassName("login-area");
		loginPanel.add(signInLink);
		RootPanel.get("loginPanelContainer").add(loginPanel);
		final StringBuilder userEmail = new StringBuilder();
		greetingService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("login -> onFailure");
			}

			@Override
			public void onSuccess(final LoginInfo result) {
				if (result.getName() != null && !result.getName().isEmpty()) {
					addGoogleAuthHelper();
					loadLogout(result);
					button.setEnabled(true);
					textBox.setEnabled(true);
					textBox.setFocus(true);
					textBox.selectAll();
				} else {
					loadLogin(result);
				}
				userEmail.append(result.getEmailAddress());
			}
			

		});
		
		
		//setting Panel's specs
				vPanel.setPixelSize(0, 40);
				hPanel.setPixelSize(100, 40);
				hPanel.add(textBox);
				hPanel.add(button);
				
				// Default location LatLng
				final LatLng myLatLng = LatLng.create(49.2500, -123.1000);
				
				//Creating map object
			    MapOptions myOptions = MapOptions.create();
			    myOptions.setZoom(12.0);
			    myOptions.setCenter(myLatLng);
			    myOptions.setMapTypeId(MapTypeId.ROADMAP);
			    map = GoogleMap.create(Document.get().getElementById("map_canvas"), myOptions);
			    
			
			    
			    // Produce the output when clicking the search button
			    button.addClickHandler(new ClickHandler(){ 

					@Override
					public void onClick(ClickEvent event) {
						
						
						// Content of the searchbox
						String a = textBox.getText();
						
						// Request object
						GeocoderRequest request = GeocoderRequest.create();
						request.setAddress(a);
						
						// Creating the Geocoder
						Geocoder geocoder = Geocoder.create();
						geocoder.geocode(request, new Geocoder.Callback(){
							
							@Override
							public void handle(JsArray<GeocoderResult> a, GeocoderStatus b) {
								
								// 
								if(b != GeocoderStatus.OK) {
									alertWidget("Invalid Address Location",
											"Please check for errors and enter again.")
											.center();
								}
								
								map.clearProjectionChangedListeners();
								// Ensure request was successful in the server
								if(b == GeocoderStatus.OK){
									
									final GeocoderResult reqll = a.get(0);
									map.setCenter(reqll.getGeometry().getLocation());
									map.setZoom(14);
									
									// Searching Bounds
									LatLngBounds bounds = LatLngBounds.create(LatLng.create((reqll.getGeometry().getLocation().lat()-0.05), (reqll.getGeometry().getLocation().lng()-0.05)), 
									LatLng.create((reqll.getGeometry().getLocation().lat()+0.05), (reqll.getGeometry().getLocation().lng()+005)));
									
									// List containing all the ParkingMeter objects
									List<ParkingMeter> result = data.result();
									
									// Plot the parking locations in the map with markers
									for(final ParkingMeter d: result){
										
										if(bounds.contains(d.getLatLng())){
											Marker parkingMarker = Marker.create();
											parkingMarker.setMap(map);
											parkingMarker.setPosition(d.getLatLng());
											
											
											// Open the InfoWindow object of a parking location when marker is clicked
											parkingMarker.addClickListener(new Marker.ClickHandler() {
												
												@Override
												public void handle(MouseEvent event) {
													
													InfoWindowOptions windowOpts = InfoWindowOptions.create();
													InfoWindow parkingInfo = InfoWindow.create(windowOpts);
													parkingInfo.setPosition(d.getLatLng());
													parkingInfo.open(map);
													parkingInfo.setContent("Cost: " + d.getPrice());
													parkingInfo.clearCloseClickListeners();
												
													
												}
											});	
								}
									}
								       }
							}
						});
					}
			    });
			    
			    
			    vPanel.add(hPanel);
				RootPanel.get("map_top").add(vPanel);
			    
			}
			// Custom error message code taken from::
				// http://stackoverflow.com/questions/5051643/messagebox-in-gwt
				public static DialogBox alertWidget(final String header,
						final String content) {
					final DialogBox box = new DialogBox();
					final VerticalPanel panel = new VerticalPanel();
					box.setText(header);
					panel.add(new Label(content));
					final Button buttonClose = new Button("Close", new ClickHandler() {
						@Override
						public void onClick(final ClickEvent event) {
							box.hide();
						}
					});
					// few empty labels to make widget larger
					final Label emptyLabel = new Label("");
					emptyLabel.setSize("auto", "25px");
					panel.add(emptyLabel);
					panel.add(emptyLabel);
					buttonClose.setWidth("90px");
					panel.add(buttonClose);
					panel.setCellHorizontalAlignment(buttonClose, HasAlignment.ALIGN_RIGHT);
					box.add(panel);
					return box;
				}		

			
			
		}


