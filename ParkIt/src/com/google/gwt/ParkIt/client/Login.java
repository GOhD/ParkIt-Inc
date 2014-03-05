package com.google.gwt.ParkIt.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class Login extends Composite {
	private Label lblNewLabel_1;
	private Label lblNewLabel_2;
	private TextBox textBoxUsername;
	private TextBox textBoxPassword;

	public Login() {
		
		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		verticalPanel.setSize("299px", "127px");
		
		Label lblNewLabel = new Label("Sign into your account:");
		lblNewLabel.setStyleName("gwt-Label-login");
		verticalPanel.add(lblNewLabel);
		lblNewLabel.setHeight("26px");
		
		FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		flexTable.setSize("306px", "110px");
		
		lblNewLabel_1 = new Label("Username: ");
		lblNewLabel_1.setStyleName("gwt-Label-login");
		flexTable.setWidget(0, 0, lblNewLabel_1);
		lblNewLabel_1.setWidth("71px");
		
		textBoxUsername = new TextBox();
		flexTable.setWidget(0, 1, textBoxUsername);
		textBoxUsername.setSize("160px", "10px");
		
		lblNewLabel_2 = new Label("Password:");
		lblNewLabel_2.setStyleName("gwt-Label-login");
		flexTable.setWidget(1, 0, lblNewLabel_2);
		lblNewLabel_2.setWidth("64px");
		
		textBoxPassword = new TextBox();
		flexTable.setWidget(1, 1, textBoxPassword);
		textBoxPassword.setSize("160px", "10px");
		
		CheckBox chckbxNewCheckBox = new CheckBox("Remember me on this computer");
		chckbxNewCheckBox.setStyleName("gwt-Label-checkbox");
		flexTable.setWidget(2, 1, chckbxNewCheckBox);
		
		Button btnNewButton = new Button("New button");
		btnNewButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (textBoxUsername.getText().length() == 0 || textBoxPassword.getText().length() == 0) {
					Window.alert("Username or password is empty."); 
				}
			}
		});
		btnNewButton.setText("Sign In");
		flexTable.setWidget(3, 1, btnNewButton);
	}

}
