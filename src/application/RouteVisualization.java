/** Class to aid with route visualization for search
 *  
 * @author UCSD MOOC development team
 *
 */

package application;

import java.util.ArrayList;
import java.util.List;

import gmapsfx.javascript.IJavascriptRuntime;
import gmapsfx.javascript.JavascriptArray;
import gmapsfx.javascript.JavascriptRuntime;
import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.LatLongBounds;
import gmapsfx.javascript.object.Marker;
import gmapsfx.javascript.object.MarkerOptions;
import gmapsfx.shapes.Circle;
import gmapsfx.shapes.Rectangle;
import gmapsfx.shapes.RectangleOptions;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import netscape.javascript.JSObject;
import roadgraph.Incident;

public class RouteVisualization {
	List<geography.GeographicPoint> points;
	List<Rectangle> incidentsArea;
    ArrayList<Marker> markerList;
	MarkerManager manager;
	JavascriptArray markers;
    IJavascriptRuntime runtime;




	public RouteVisualization(MarkerManager manager) {
        points = new ArrayList<geography.GeographicPoint>();
        incidentsArea = new ArrayList<Rectangle>();
        markerList = new ArrayList<Marker>();
		this.manager = manager;

	}

    public void acceptPoint(geography.GeographicPoint point) {
    	points.add(point);

        // System.out.println("accepted point : " + point);
    }



    public void startVisualization() {

    	System.out.println("Start visualization");
    	
    	LatLongBounds bounds = new LatLongBounds();
    	List<LatLong> latLongs = new ArrayList<LatLong>();
    	JavascriptArray jsArray = new JavascriptArray();
    	manager.hideIntermediateMarkers();
        manager.hideDestinationMarker();
//    	manager.disableRouteButtons(true);

    	// create JavascriptArray of points
    	for(geography.GeographicPoint point : points) {
    		
    		LatLong ll = new LatLong(point.getX(), point.getY());
    		Marker newMarker = null;
    		
    		//Check if Incident or not
    		if(Incident.class.isInstance(point)){
    			Incident i = (Incident) point;
    			newMarker = new Marker(MarkerManager.createDefaultIncident(ll));
        		manager.getMap().addMarker(newMarker);
    			System.out.println("Adding an Incident to print" + ll.toString());
    			
    			//Add incident area to the map
    			LatLongBounds llb = new LatLongBounds(
    					new LatLong(
    							i.getSurrounding().getSouthLatitude(), 
    							i.getSurrounding().getWestLongitude()),
    					new LatLong(
    							i.getSurrounding().getNorthLatitude(),
    							i.getSurrounding().getEastLongitude()));
    			RectangleOptions ro = new RectangleOptions();
    			ro.bounds(llb);
    			Rectangle incidentArea = new Rectangle(ro);
    			this.incidentsArea.add(incidentArea);
    			
    			manager.getMap().addMapShape(incidentArea);
    			
    		}
    		else {
    			newMarker = new Marker(MarkerManager.createDefaultOptions(ll));
                jsArray.push(newMarker);
    			System.out.println("Adding an Intersection to print" + ll.toString());
    		}
    		
            markerList.add(newMarker);
            bounds.extend(ll);
    	}

    	// fit map bounds to visualization
    	//manager.getMap().fitBounds(bounds);

        // get javascript runtime and execute animation
    	runtime = JavascriptRuntime.getInstance();
    	String command = runtime.getFunction("visualizeSearch", manager.getMap(), jsArray);
    	// System.out.println(command);

    	runtime.execute(command);

//    	MapApp.showInfoAlert("Nodes visited :"  , latLongs.size() +" nodes were visited in the search");
    	manager.disableVisButton(true);
//        manager.disableRouteButtons(false);

    }

    public void clearMarkers() {
    	for(Marker marker : markerList) {
    		marker.setVisible(false);
    	}
    	for(Rectangle r: incidentsArea) {
    		r.setVisible(false);
    	}
    }


}
