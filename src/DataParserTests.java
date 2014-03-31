import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import com.google.gwt.ParkIt.server.ParkingDataRetriever;
import com.google.gwt.ParkIt.shared.LatLong;
import com.google.gwt.ParkIt.shared.MeterEntry;

// hack to respect private method parseData
class DataRetrieverHack extends ParkingDataRetriever {
	public Collection<MeterEntry> parseData(String data, char separator) {
		return super.parseData(data, separator);
	}
}

public class DataParserTests {
	DataRetrieverHack dr = new DataRetrieverHack();
	
	@Test
	public void assertEntriesAreValid()
	{
		Collection<MeterEntry> entries = null;
		LatLong ll = new LatLong(0,0); 
		entries.add(new MeterEntry("a", ll, 3.0));
		entries.add(new MeterEntry("a", ll, 3.0));

		assertNotNull(entries);
		for (MeterEntry entry : entries) {
			System.out.println(entry);
			assertNotNull(entry.getName());
			LatLong latLong = entry.getLatLong();
			assertNotNull(latLong);
			assertTrue(latLong.getLat() != 0 && latLong.getLong() != 0);
			assertNotNull(entry.getRate());
		}
	}



}
