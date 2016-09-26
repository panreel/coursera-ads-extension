package week5_apiheuristic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import week5_apiheuristic.BingTrafficAPIService.MapArea;

public class BingTrafficAPIServiceTest {

	public static BingTrafficAPIService BingTraffic;
	public static Gson gson; 
	//San Francisco Test Area
	public MapArea mapArea_SF = new BingTrafficAPIService.MapArea(37.77888,-122.44581,37.80248,-122.40392);
	//Los Angeles Test Area
	public MapArea mapArea_LA = new BingTrafficAPIService.MapArea(34.020849,-118.288992,34.078173,-118.209538);
	//San Diego Test Area
	public MapArea mapArea_SD = new BingTrafficAPIService.MapArea(32.588582,-117.245060, 32.795829,-117.103003);

	@BeforeClass
	public static void initService() {
		try {
			BingTraffic = new BingTrafficAPIService();
			gson = new Gson();
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSanFrancisco() {
		//Testing SF
		testCity("San Francisco", mapArea_SF);
	}
	
	@Test
	public void testLosAngeles() {
		//Testing LA
		testCity("Los Angeles", mapArea_LA);
	}

	@Test
	public void testSanDiego() {
		//Testing SD
		testCity("San Diego", mapArea_SD);
	}
	
	/***
	 * Base function for city API testing
	 * @param cityName Name of the city [debug purpose]
	 * @param mapArea The coordinates of the city area to inspect
	 */
	public void testCity(String cityName, MapArea mapArea) {
		try {
			
			//Querying API
			System.out.println("----");
			System.out.println("Testing " + cityName + " Traffic...");
			List<MapArea> trafficData = BingTraffic.getTrafficFlowData(mapArea);
			System.out.println("Generating MapAreas...");
			double latIncrement = 360 / 24901;
			for(MapArea a : trafficData) {
				System.out.println(a.toString());
				System.out.println("----");
				System.out.println("Testing random points...");
				//Test five random points for each incident MapArea
				for(int j = 0; j < 5; j++) {
					//generate random test latitude
					double testLat = 
							(a.getSouthLatitude() - latIncrement) + 
							Math.random() * (a.getNorthLatitude() - a.getSouthLatitude() + 2 * latIncrement);
					double testLon = 
							(a.getEastLongitude() - latIncrement) + 
							Math.random() * (a.getWestLongitude() - a.getEastLongitude() + 2 * latIncrement + 1);
					//check if contained in MapArea
					System.out.println("Is " + testLat + " - " + testLon + " contained in this area?");
					System.out.println(a.contains(testLat, testLon));
				}
			}
			System.out.println("----");
			
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
