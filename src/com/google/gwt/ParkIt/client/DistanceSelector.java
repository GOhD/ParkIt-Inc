package com.google.gwt.ParkIt.client;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.ParkIt.client.GoogleLoginService;
import com.google.gwt.ParkIt.client.GoogleLoginServiceAsync;
import com.google.gwt.ParkIt.client.ParkIt;
import com.google.gwt.ParkIt.shared.FieldVerifier;
import com.google.gwt.ParkIt.shared.MeterEntry;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SimpleRadioButton;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;

public class DistanceSelector {

	private FlexTable addressesFlexTable;
	private HorizontalPanel addPanel;
	private TextBox newAddressTextBox;
	private Button addButton;
	private ArrayList <String> addresses = new ArrayList<String>();
	private ArrayList<RadioButton> radioButtons;
	private int[] distances;
	private int selectedIndex;
	private ParkIt observer;
	private SimpleRadioButton simpleRadioButton;

	public DistanceSelector(int[] distances, int selectedIndex, ParkIt observer) {
		radioButtons = new ArrayList<RadioButton>(distances.length);
		for (int distance : distances) {
			RadioButton radiobutton = new RadioButton("Distance", "" + distance);
			this.radioButtons.add(radiobutton);
		}
		this.distances = distances;
		this.selectedIndex = selectedIndex;
		this.observer = observer;
	}

	public void displayDistanceSelector() {
		RadioButton selectedRadioButton = radioButtons.get(selectedIndex);
		selectedRadioButton.setValue(true);

		// Add toggle button to the root panel.
		VerticalPanel favoritesPanel = new VerticalPanel();
		favoritesPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		Label favorites = new Label("Favorites:");
		favoritesPanel.add(favorites);
		favoritesPanel.setSpacing(5);
		addressesFlexTable = new FlexTable();
		addressesFlexTable.setText(0, 0, "");
		addressesFlexTable.setText(0, 1, "Address");
		addressesFlexTable.setText(0, 2, "Remove");


		favoritesPanel.add(addressesFlexTable);
		addressesFlexTable.setHeight("40px");
		addPanel = new HorizontalPanel();
		addPanel.setSize("200px", "26px");
		favoritesPanel.add(addPanel);
		newAddressTextBox = new TextBox();
		newAddressTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER){
					addAddress();
				}
			}
		});

		addPanel.add(newAddressTextBox);
		newAddressTextBox.setWidth("140px");

		addButton = new Button("New button");
		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addAddress();
			}
		});

		addButton.setText("Add");
		addPanel.add(addButton);
		addButton.setWidth("45px");

		addressesFlexTable.getRowFormatter().addStyleName(0, "addressListHeader");
		addressesFlexTable.addStyleName("addressList");
		addressesFlexTable.getCellFormatter().addStyleName(0, 0, "addressListRemoveColumn");
		addressesFlexTable.getCellFormatter().addStyleName(0, 1, "addressListRemoveColumn");
		addressesFlexTable.getCellFormatter().addStyleName(0, 2, "addressListRemoveColumn");

		//		Label radius = new Label("Radius:");
		//		distancePanel.add(radius);
		//		distancePanel.spacing(5);
		//		for (RadioButton radiobutton : radioButtons) {
		//			distancePanel.add(radiobutton);
		//		}
		//		Button updateButton = new Button("Update");
		//		updateButton.setStylePrimaryName("btn"); // use Bootstrap button style
		//		ClickHandler clickHandler = new ClickHandler(){
		//
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				if (radioButtons.get(selectedIndex).getValue() == false) {
		//					for (int i = 0; i < distances.length; i++) {
		//						if (radioButtons.get(i).getValue()) {
		//							selectedIndex = i;
		//							observer.updateMapEntries();
		//							break;
		//						}
		//					}
		//				}
		//				
		//			}
		//		};
		//		
		//		updateButton.addClickHandler(clickHandler);
		//		distancePanel.add(updateButton);

		RootPanel.get("options_panel").add(favoritesPanel, 0, 0);
		favoritesPanel.setSize("300px", "70px");
	}

	private void addAddress() {
		final String address = newAddressTextBox.getText().trim();
		newAddressTextBox.setFocus(true);

		// Address code must be between 1 and 15 chars that are numbers, letters, or dots.
		if (!address.matches("^[0-9a-zA-Z \\.]{1,30}$")) {
			Window.alert("'" + address + "' is not a valid address.");
			newAddressTextBox.selectAll();
			return;
		}
		newAddressTextBox.setText("");

		//Don't add the address if it's already in the table.
		if (addresses.contains(address)) {
			Window.alert("That address is already stored in favorites!");
			return;
		}
		//Add the address to the table.
		int row = addressesFlexTable.getRowCount();
		addresses.add(address);
		addressesFlexTable.setText(row, 1, address);
		// Add a button to remove this address from the table.
		Button removeAddress = new Button("x");
		addressesFlexTable.setWidget(row, 2, removeAddress);
		addressesFlexTable.setWidget(row, 0, simpleRadioButton);
		simpleRadioButton = new SimpleRadioButton("new name");
		// Add styles to elements in the address list table.
		addressesFlexTable.getCellFormatter().addStyleName(0, 0, "addressListRemoveColumn");
		addressesFlexTable.getCellFormatter().addStyleName(0, 1, "addressListRemoveColumn");
		addressesFlexTable.getCellFormatter().addStyleName(0, 2, "addressListRemoveColumn");
		removeAddress.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {					
				int removedIndex = addresses.indexOf(address);
				addresses.remove(removedIndex);
				addressesFlexTable.removeRow(removedIndex + 1);
			}
		});	    
	}

	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GoogleLoginServiceAsync googleLoginService = GWT
			.create(GoogleLoginService.class);


	public void displayDistanceSelectorAdmin() {
		RadioButton selectedRadioButton = radioButtons.get(selectedIndex);
		selectedRadioButton.setValue(true);

		final Label errorLabel = new Label();

		// Add toggle button to the root panel.
		VerticalPanel distancePanel = new VerticalPanel();
		distancePanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);

		//		Label favorites = new Label("Favorites:");
		//		distancePanel.add(favorites);
		//		distancePanel.setSpacing(10);
		//		Label radius = new Label("Radius:");
		//		distancePanel.add(radius);
		//		distancePanel.setSpacing(5);
		//		for (RadioButton radiobutton : radioButtons) {
		//			distancePanel.add(radiobutton);
		//		}
		//		Button updateButton = new Button("Update");
		//		updateButton.setStylePrimaryName("btn"); // use Bootstrap button style
		//		
		//		ClickHandler clickHandler = new ClickHandler(){
		//
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				if (radioButtons.get(selectedIndex).getValue() == false) {
		//					for (int i = 0; i < distances.length; i++) {
		//						if (radioButtons.get(i).getValue()) {
		//							selectedIndex = i;
		//							observer.updateMapEntries();
		//							break;
		//						}
		//					}
		//				}
		//				
		//			}
		//		};
		//		
		//		updateButton.addClickHandler(clickHandler);
		//		distancePanel.add(updateButton);

		RootPanel.get("options_panel").add(distancePanel);

		Label space = new Label("Admin:");
		distancePanel.add(space);
		distancePanel.setSpacing(5);

		final Button dataButton = new Button("Data");
		dataButton.setStylePrimaryName("btn"); // use Bootstrap button style


		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Updating Data");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Updating Data:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				dataButton.setEnabled(true);
				dataButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				//fetch meter data
				meterDataRetrieval();

				//popup
				sendDataToServer();

			}


			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					//fetch meter data
					meterDataRetrieval();

					//popup
					sendDataToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */

			private void sendDataToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = "Vancouver Parking Meter";
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter proper data to input!");
					return;
				}

				// Then, we send the input to the server.
				dataButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				googleLoginService.greetServer(textToServer,
						new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox
						.setText("Data Upload - Failure");
						serverResponseLabel
						.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Data Upload");
						serverResponseLabel
						.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
			}
		}
		MyHandler handler = new MyHandler();

		dataButton.addClickHandler(handler);
		distancePanel.add(dataButton);
		distancePanel.add(errorLabel);

		RootPanel.get("options_panel").add(distancePanel);
	}


	public double getRadius() {
		for (int i = 0; i < distances.length; i++) {
			if (radioButtons.get(i).getValue()) return distances[i];
		}
		return 20.0; // default radius
	}

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
	}

}
