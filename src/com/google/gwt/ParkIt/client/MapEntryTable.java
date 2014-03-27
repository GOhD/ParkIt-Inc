package com.google.gwt.ParkIt.client;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MapEntry;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;

public class MapEntryTable {

	public static void displayMapEntryTable(Collection<MapEntry> entries) {
		// Create CellTable.
		CellTable<MapEntry> table = new CellTable<MapEntry>();

		// Create type column.
		TextColumn<MapEntry> typeColumn = new TextColumn<MapEntry>() {
			@Override
			public String getValue(MapEntry mapEntry) {
				return mapEntry.getType();
			}
		};

		// Create lat column.
		NumberCell latCell = new NumberCell();
		Column<MapEntry, Number> latColumn = new Column<MapEntry, Number>(latCell) {
			@Override
			public Number getValue(MapEntry mapEntry) {
				LatLong latLong = mapEntry.getLatLong();
				return latLong.getLat();
			}
		};

		// Create lon column.
		NumberCell lonCell = new NumberCell();
		Column<MapEntry, Number> lonColumn = new Column<MapEntry, Number>(lonCell) {
			@Override
			public Number getValue(MapEntry mapEntry) {
				LatLong latLong = mapEntry.getLatLong();
				return latLong.getLong();		      }
		};


	    // Make the type column sortable.
	    // Note: the table is sorted on the client side.
	    typeColumn.setSortable(true);
	    latColumn.setSortable(true);
	    lonColumn.setSortable(true);
	    
	    // Add the columns.
	    table.addColumn(typeColumn, "Type");
	    table.addColumn(latColumn, "Lat");
	    table.addColumn(lonColumn, "Long");
	    

	    // Create a data provider.
	    ListDataProvider<MapEntry> dataProvider = new ListDataProvider<MapEntry>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(table);

	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<MapEntry> list = dataProvider.getList();
	    for (MapEntry MapEntry : entries) {
	      list.add(MapEntry);
	    }

	    // Add a ColumnSortEvent.ListHandler to connect sorting to the
	    // java.util.List.
	    
	    // type Column sorting
	    ListHandler<MapEntry> typeColumnSortHandler = new ListHandler<MapEntry>(list);
	    typeColumnSortHandler.setComparator(typeColumn,
	        new Comparator<MapEntry>() {
	          public int compare(MapEntry o1, MapEntry o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            // Compare the type columns.
	            if (o1 != null) {
	            	if (o2 != null) {
	            		return o1.getType().compareTo(o2.getType());
	            	}
	            	return 1;
	            }
	            return -1;
	          }
	        });
	    table.addColumnSortHandler(typeColumnSortHandler);
	    
	    // lat Column sorting
	    ListHandler<MapEntry> latColumnSortHandler = new ListHandler<MapEntry>(list);
	    latColumnSortHandler.setComparator(latColumn,
	        new Comparator<MapEntry>() {
	          public int compare(MapEntry o1, MapEntry o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            // Compare the lat columns.
	            if (o1 != null) {
	            	if (o2 != null) {
	            		LatLong l1 = o1.getLatLong();
	            		LatLong l2 = o2. getLatLong();
	            		return (l1.getLat() < l2.getLat()) ? -1 : 1;
	            	}
	            	return 1;
	            }
	            return -1;
	          }
	        });
	    table.addColumnSortHandler(latColumnSortHandler);

	    // lon Column sorting
	    ListHandler<MapEntry> lonColumnSortHandler = new ListHandler<MapEntry>(list);
	    lonColumnSortHandler.setComparator(lonColumn,
	        new Comparator<MapEntry>() {
	          public int compare(MapEntry o1, MapEntry o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            // Compare the lon columns.
	            if (o1 != null) {
	            	if (o2 != null) {
	            		LatLong l1 = o1.getLatLong();
	            		LatLong l2 = o2. getLatLong();
	            		return (l1.getLong() < l2.getLong()) ? -1 : 1;
	            	}
	            	return 1;
	            }
	            return -1;
	          }
	        });
	    table.addColumnSortHandler(lonColumnSortHandler);
	    
	    // We know that the data is sorted alphabetically by default.
	    table.getColumnSortList().push(typeColumn);
	    table.getColumnSortList().push(latColumn);
	    table.getColumnSortList().push(lonColumn);
		
		// arbitrary visible range of table; don't set it too high or performance will suffer
		table.setVisibleRange(0, 100);
		
		Style style = table.getElement().getStyle();
		style.setProperty("margin", "auto");
		
		// Add it to the root panel.
		RootPanel.get("table").add(table);
	}
}

