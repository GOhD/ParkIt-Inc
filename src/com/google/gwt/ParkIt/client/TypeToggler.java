package com.google.gwt.ParkIt.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TypeToggler {
	
	private ArrayList<TypeCheckBox> typeCheckBoxes;
	private ArrayList<String> selectedTypes;
	private ParkIt observer;
	
	public TypeToggler(ArrayList<String> types, ParkIt observer) {
		typeCheckBoxes = new ArrayList<TypeCheckBox>();
		for (String type : types) {
			TypeCheckBox typeCheckBox = new TypeCheckBox(type);
			typeCheckBox.setValue(true);
			typeCheckBoxes.add(typeCheckBox);
		}
		this.selectedTypes = types;
		this.observer = observer;
	}
	
	public void displayTypeToggler() {
			ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getSelectedTypes().equals(selectedTypes) == false) {
					selectedTypes = getSelectedTypes();
					observer.updateMapEntries();
				}
			}
		};
		Button updateButton = new Button("Update");
		updateButton.setStylePrimaryName("btn"); // use Bootstrap button style
		updateButton.addClickHandler(clickHandler);
	}

	
	public ArrayList<String> getSelectedTypes() {
		ArrayList<String> selectedTypes = new ArrayList<String> (typeCheckBoxes.size());
		for (TypeCheckBox typeCheckBox : typeCheckBoxes) {
			if (typeCheckBox.getValue()) selectedTypes.add(typeCheckBox.getType());
		}
		return selectedTypes;
	}	
}