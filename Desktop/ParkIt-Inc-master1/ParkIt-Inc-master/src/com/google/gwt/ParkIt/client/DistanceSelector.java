package com.google.gwt.ParkIt.client;

import java.util.ArrayList;

import com.google.gwt.ParkIt.client.ParkIt;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DistanceSelector {

	private ArrayList<RadioButton> radioButtons;
	private int[] distances;
	private int selectedIndex;
	private ParkIt observer;

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
		VerticalPanel distancePanel = new VerticalPanel();
		distancePanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		Label favorites = new Label("Favorites:");
		distancePanel.add(favorites);
		distancePanel.setSpacing(5);
		Label radius = new Label("Radius:");
		distancePanel.add(radius);
		distancePanel.setSpacing(5);
		for (RadioButton radiobutton : radioButtons) {
			distancePanel.add(radiobutton);
		}
		Button updateButton = new Button("Update");
		updateButton.setStylePrimaryName("btn"); // use Bootstrap button style
		ClickHandler clickHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if (radioButtons.get(selectedIndex).getValue() == false) {
					for (int i = 0; i < distances.length; i++) {
						if (radioButtons.get(i).getValue()) {
							selectedIndex = i;
							observer.updateMapEntries();
							break;
						}
					}
				}
				
			}
		};
		
		updateButton.addClickHandler(clickHandler);
		distancePanel.add(updateButton);

		RootPanel.get("options_panel").add(distancePanel);
	}
	
	public void displayDistanceSelectorAdmin() {
		RadioButton selectedRadioButton = radioButtons.get(selectedIndex);
		selectedRadioButton.setValue(true);

		// Add toggle button to the root panel.
		VerticalPanel distancePanel = new VerticalPanel();
		distancePanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		Label favorites = new Label("Favorites:");
		distancePanel.add(favorites);
		distancePanel.setSpacing(5);
		Label radius = new Label("Radius:");
		distancePanel.add(radius);
		distancePanel.setSpacing(5);
		for (RadioButton radiobutton : radioButtons) {
			distancePanel.add(radiobutton);
		}
		Button updateButton = new Button("Update");
		updateButton.setStylePrimaryName("btn"); // use Bootstrap button style
		ClickHandler clickHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if (radioButtons.get(selectedIndex).getValue() == false) {
					for (int i = 0; i < distances.length; i++) {
						if (radioButtons.get(i).getValue()) {
							selectedIndex = i;
							observer.updateMapEntries();
							break;
						}
					}
				}
				
			}
		};
		
		updateButton.addClickHandler(clickHandler);
		distancePanel.add(updateButton);

		RootPanel.get("options_panel").add(distancePanel);

		Label space = new Label("Admin:");
		distancePanel.add(space);
		distancePanel.setSpacing(5);
	
		Button dataButton = new Button("Data");
		dataButton.setStylePrimaryName("btn"); // use Bootstrap button style

		dataButton.addClickHandler(clickHandler);
		distancePanel.add(dataButton);

		RootPanel.get("options_panel").add(distancePanel);
	}
	
	public double getRadius() {
		for (int i = 0; i < distances.length; i++) {
			if (radioButtons.get(i).getValue()) return distances[i];
		}
		return 20.0; // default radius
	}

}
