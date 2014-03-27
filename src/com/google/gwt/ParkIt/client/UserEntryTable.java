package com.google.gwt.ParkIt.client;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.ParkIt.shared.UserEntry;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;

public class UserEntryTable {
	public interface UserEntryTableDelegate {
	}

	public static void displayUserEntryTable(Collection<UserEntry> entries, final UserEntry myUser, final UserEntryTableDelegate delegate) {
		// Create CellTable.
		final CellTable<UserEntry> table = new CellTable<UserEntry>();

		// Create name column.
		TextColumn<UserEntry> nameColumn = new TextColumn<UserEntry>() {
			@Override
			public String getValue(UserEntry userEntry) {
				return userEntry.getName();
			}
		};

		// Create email column.
		TextColumn<UserEntry> emailColumn = new TextColumn<UserEntry>() {
			@Override
			public String getValue(UserEntry userEntry) {
				return userEntry.getEmail();
			}
		};

		
		final AsyncCallback<Void> voteCallback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(Void result) {
				table.redraw();
			}
		};

		

		// Create a data provider.
		ListDataProvider<UserEntry> dataProvider = new ListDataProvider<UserEntry>();

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(table);

		// Add the data to the data provider, which automatically pushes it to the
		// widget.
		List<UserEntry> list = dataProvider.getList();
		for (UserEntry UserEntry : entries) {
			list.add(UserEntry);
		}

		// Add a ColumnSortEvent.ListHandler to connect sorting to the
		// java.util.List.

		// name Column sorting
		ListHandler<UserEntry> nameColumnSortHandler = new ListHandler<UserEntry>(list);
		nameColumnSortHandler.setComparator(nameColumn,
				new Comparator<UserEntry>() {
			public int compare(UserEntry o1, UserEntry o2) {
				if (o1 == o2) {
					return 0;
				}

				// Compare the type columns.
				if (o1 != null) {
					if (o2 != null) {
						return o1.getName().compareTo(o2.getName());
					}
					return 1;
				}
				return -1;
			}
		});
		table.addColumnSortHandler(nameColumnSortHandler);

		// email Column sorting
		ListHandler<UserEntry> emailColumnSortHandler = new ListHandler<UserEntry>(list);
		emailColumnSortHandler.setComparator(emailColumn,
				new Comparator<UserEntry>() {
			public int compare(UserEntry o1, UserEntry o2) {
				if (o1 == o2) {
					return 0;
				}

				// Compare the type columns.
				if (o1 != null) {
					if (o2 != null) {
						return o1.getEmail().compareTo(o2.getEmail());
					}
					return 1;
				}
				return -1;
			}
		});
		table.addColumnSortHandler(emailColumnSortHandler);


		table.getColumnSortList().push(nameColumn);
		table.getColumnSortList().push(emailColumn);

		// arbitrary visible range of table; don't set it too high or performance will suffer
		table.setVisibleRange(0, 100);

		Style style = table.getElement().getStyle();
		style.setProperty("margin", "auto");

		// Add it to the root panel.
		RootPanel.get("table").add(table);
	}

}
