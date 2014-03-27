package com.google.gwt.ParkIt.client;

import com.google.gwt.user.client.ui.CheckBox;

public class TypeCheckBox extends CheckBox {

	private String type;
	
	public TypeCheckBox(String type) {
		super(type);
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
