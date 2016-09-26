package week5_apiheuristic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import week5_apiheuristic.APIUtils.HTTPMethods;
import week5_apiheuristic.BingTrafficAPIService.IncidentPoint.Point;

public class BingTrafficAPIService extends APIService {

	private Gson gson;
	
	/**
	 * Number of miles far from an incident for each level of severity
	 */
	private static double MILES_SEVERITY = 0.1;
	/**
	 * Severity increment if road closed
	 */
	private static double CLOSEDROAD_INCREMENT = 0.1;
	/**
	 * Earth Circumference in miles
	 */
	private static double EARTH_CIRCUM = 24901;
	
	/**
	 * Create a new HereTraffic API service handler
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 * @throws FileNotFoundException
	 */
	public BingTrafficAPIService() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		super("api_configs/BingTraffic.config.json");
		gson = new Gson();
	}
	
	/**
	 * @param mapArea inspect traffic in a specific area
	 * @return List of mapArea with incidents
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 */
	public List<MapArea> getTrafficFlowData(MapArea mapArea) throws ClientProtocolException, URISyntaxException, IOException{
		
		String endpoint = "Incidents/" + mapArea.toEndPointFormat();
		List<NameValuePair> queryString = new LinkedList<NameValuePair>();
		queryString.add(new BasicNameValuePair("key", this.getAppCode()));
		
		JsonObject incidents = this.doAPIRequest(HTTPMethods.GET, endpoint, queryString, null, null);
		return this.extractIncidentMapAreas(incidents);
		
	}
	
	/**
	 * Extract MapAreas from the returned JSON
	 * @param incident map in JSON format
	 * @return incidents List of MapArea with the crowded and incident zones
	 */
	private List<MapArea> extractIncidentMapAreas(JsonObject incidents) {
		
		List<MapArea> IncidentsMapArea = new LinkedList<MapArea>();
		
		JsonObject resources = incidents.getAsJsonArray("resourceSets").get(0).getAsJsonObject();
		int estimatedIncidents = resources.getAsJsonPrimitive("estimatedTotal").getAsInt();
		
		if(estimatedIncidents > 0) {
			for(int i = 0; i < estimatedIncidents; i++)
			{
				JsonElement resource = resources.getAsJsonArray("resources").get(i);
				IncidentPoint current_incident = gson.fromJson(resource, IncidentPoint.class);
				IncidentsMapArea.add(current_incident.generateMapArea());
			}
		}
		
		return IncidentsMapArea;
	}
	
	/**
	 * Class used to represent a Map Area for detecting traffic flow
	 * @author nico
	 */
	public static class MapArea {
		
		private double SouthLatitude;
		private double WestLongitude; 
		private double NorthLatitude; 
		private double EastLongitude;
		private double latCenter;
		private double lonCenter;
		private int severity;
		
		public MapArea(double sL, double wL, double nL, double eL) {
			this.SouthLatitude = sL;
			this.WestLongitude = wL;
			this.NorthLatitude = nL;
			this.EastLongitude = eL;
		}
		
		public double getSouthLatitude() {
			return this.SouthLatitude;
		}
		
		public void updateSouthLatitude(double sL) {
			this.SouthLatitude = sL;
		}
		
		public double getNorthLatitude() {
			return this.NorthLatitude;
		}
		
		public void updateNorthLatitude(double nL) {
			this.NorthLatitude = nL;
		}
		
		public double getWestLongitude(){
			return this.WestLongitude;
		}
		
		public void updateWestLongitude(double wL) {
			this.WestLongitude = wL;
		}
		
		public double getEastLongitude(){
			return this.EastLongitude;
		}
		
		public void updateEastLongitude(double eL) {
			this.EastLongitude = eL;
		}
		
		public double getLatCenter() {
			return latCenter;
		}

		public void setLatCenter(double latCenter) {
			this.latCenter = latCenter;
		}

		public double getLonCenter() {
			return lonCenter;
		}

		public void setLonCenter(double lonCenter) {
			this.lonCenter = lonCenter;
		}
		
		public int getSeverity() {
			return severity;
		}

		public void setSeverity(int severity) {
			this.severity = severity;
		}
		
		/**
		 * Check if a point is contained in the MapArea (works well for small areas)
		 * @param lat the point latitude
		 * @param lon the point longitude
		 * @return true if point is contained in the MapArea, false otherwise
		 */
		public boolean contains(double lat, double lon) {
			if(lat >= this.SouthLatitude && lat <= this.NorthLatitude)
				if(lon >= this.WestLongitude && lon <= this.EastLongitude)
					return true;
			return false;
		}
		
		public String toEndPointFormat() {
			return this.SouthLatitude + "," + this.WestLongitude + "," + this.NorthLatitude + "," + this.EastLongitude;
		}
		
		public String toString() {
			return "SW Point: <" + this.SouthLatitude + ", " + this.WestLongitude + ">, NE Point: <" + this.NorthLatitude + ", " + this.EastLongitude + ">";
		}
		
	}
	
	//Wrapper for Incident data - returned as a response from Traffic Flow Data call
	public class IncidentPoint {
		
		private Point point;
		private String description;
		private Boolean roadClosed;
		private Integer severity;
		private Point toPoint;
		
		public class Point {
			
			private String type;
			private List<Double> coordinates;
			
			public Point(String type, double lat, double lon) {
				this.type = type;
				this.coordinates = new ArrayList<Double>();
				this.coordinates.add(lat);
				this.coordinates.add(lon);
			}
			
			public List<Double> getCoordinates() {
				return this.coordinates;
			}
			
			public void setCoordinates(List<Double> coords) {
				this.coordinates = coords;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}
			
			public String toString() {
				return "[" + this.coordinates.get(0) + ", " + this.coordinates.get(1) + "]";
			}
		}
		
		/**
		 * Create a MapArea with Incident data
		 * @return the enclosing MapArea
		 */
		public MapArea generateMapArea() {
			//Calculate mid-point between two coords
			Point midPoint = midPoint(this.point, this.toPoint);
			//Evaluate incident seriousness in miles
			System.out.println("Incident severity: " + this.severity);
			double extendedSeverity = this.severity + (this.roadClosed ?  CLOSEDROAD_INCREMENT : 0);
			System.out.println("Incident adj severity: " + extendedSeverity);
			double miles = extendedSeverity * MILES_SEVERITY;
			System.out.println("Incident miles severity: " + miles);
			MapArea result = calculateArea(midPoint, miles);
			result.setLatCenter(midPoint.coordinates.get(0));
			result.setLonCenter(midPoint.coordinates.get(1));
			result.setSeverity(severity);
			return result;
		}
		
		/**
		 * Calculate mid point given two Points
		 * @param start Starting lat, lon pair
		 * @param end Ending lat, lon pair
		 * @return lat, lon mid-point
		 */
		private Point midPoint(Point start, Point end){

		    double dLon = Math.toRadians(end.coordinates.get(1) - start.coordinates.get(1));

		    //convert to radians
		    double lat1 = Math.toRadians(start.coordinates.get(0));
		    double lat2 = Math.toRadians(end.coordinates.get(0));
		    double lon1 = Math.toRadians(start.coordinates.get(1));

		    double Bx = Math.cos(lat2) * Math.cos(dLon);
		    double By = Math.cos(lat2) * Math.sin(dLon);
		    double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
		    double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

		    //return midPoint in degrees
		    return new Point(null, Math.toDegrees(lat3), Math.toDegrees(lon3));
		}
		
		/**
		 * Calculate a square, centered in a point, with specific distance from that point (works well on small areas)
		 * @param center center of the square
		 * @param distance distance from the center of the square
		 * @return MapArea which define the intended square 
		 */
		private MapArea calculateArea(Point center, double distance) {
			double latIncrement = distance * 360 / EARTH_CIRCUM;
			System.out.println("Lat increment is " + latIncrement);
			System.out.println("Lat here is " + center.coordinates.get(0));
			System.out.println("Cos here is " + Math.cos(Math.toRadians(center.coordinates.get(0))));
			double wholeLangAtLat = Math.cos(Math.toRadians(center.coordinates.get(0))) * EARTH_CIRCUM;
			System.out.println("Whole Longitude here is " + wholeLangAtLat);
			double lonIncrement = Math.abs(distance * 360 / wholeLangAtLat);
			System.out.println("Long increment is " + lonIncrement);
			return new MapArea(
					center.coordinates.get(0) - latIncrement,
					center.coordinates.get(1) - lonIncrement,
					center.coordinates.get(0) + latIncrement,
					center.coordinates.get(1) + lonIncrement);
		}
		
		public String toString() {
			StringBuilder ss = new StringBuilder();
			ss.append("Incident Info: ");
			ss.append(this.point.toString() + " --> " + this.toPoint.toString());
			ss.append(" // Severity: " + this.severity + ", Road Closed: " + this.roadClosed);
			return ss.toString();
		}
		
	}
	
	

}
