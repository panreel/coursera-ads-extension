/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;
import week5_apiheuristic.BingTrafficAPIService.MapArea;

/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {
	
	//XXX: First Implementation W2
	// This is the core data structure of the app.
	// We define the Intersection and Road classes in order to support Project extensibility
	private Map<Intersection, List<Road>> map;
	
	// Number of intersections in the map
	// We keep this var in order to avoid recalculate the number each time
	private int numIntersections;
	
	// Number of roads in the map
	// We keep this var in order to avoid recalculate the number each time
	private int numRoads;

	// Keep Area Bounding box for getting traffic data
	private MapArea area;
	
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph() {
		// XXX: First Implementation W2
		this.map = new HashMap<Intersection, List<Road>>();
		this.numIntersections = 0;
		this.numRoads = 0;
	}
	
	/**
	 * Get the list of roads which starts from intersection i
	 * @param i The starting intersection
	 * @return The list of roads which starts from intersection i
	 */
	public List<Road> getRoads(Intersection i) {
		return this.map.get(i);
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices() {
		//XXX: First Implementation W2
		return this.numIntersections;
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices() {
		//XXX: First Implementation W2
		return new HashSet<GeographicPoint>(this.map.keySet());
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges() {
		// XXX: First Implementation W2
		return this.numRoads;
	}
	
	/**
	 * Returns true if map contains specific point (intersection)
	 * @param gp The point to search for
	 * @return If point belongs to map
	 */
	public boolean containsVertex(GeographicPoint gp) {
		// XXX: Personal add W2
		return this.map.containsKey(gp);
	}
	
	/**
	 * Update RoadList without doing list extraction and updating every time
	 * @param intersection - start vertex
	 * @param newRoad - edge to add in the internal representation
	 * @return true if adding was ok, false otherwise
	 */
	private boolean updateRoadList(Intersection intersection, Road newRoad) {
		List<Road> roadList = null;
		if(this.map.containsKey(intersection) && (roadList = this.map.get(intersection)) != null)
		{
			roadList.add(newRoad);
			return true;
		}
		return false;
	}
	
	/***
	 * Checks if a MapArea is defined for this map.
	 * If no creates a new map. If yes, updates boundaries based on input location
	 * @param location use for updating map area boundaries
	 */
	private void checkBorders(GeographicPoint location) {
		//if no area is defined check when adding first intersection
		if(area == null)
			this.area = new MapArea(location.x, location.y, location.x, location.y);
		else {
			if(location.y < area.getWestLongitude()) area.updateWestLongitude(location.y);
			if(location.y > area.getEastLongitude()) area.updateEastLongitude(location.y);
			if(location.x < area.getSouthLatitude()) area.updateSouthLatitude(location.x);
			if(location.x > area.getNorthLatitude()) area.updateNorthLatitude(location.x);
		}
	}
	
	/**
	 * Get the bounding box for the selected Map
	 * @return the intended bounding box
	 */
	public MapArea getBoundingBox() {
		return this.area;
	}

	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location) {
		// XXX: First Implementation W2
		if(location != null && !this.containsVertex(location))
		{
			List<Road> roads = new ArrayList<Road>();
			this.map.put(new Intersection(location), roads);
			//Update number of intersections
			this.numIntersections++;
			//Update MapArea bounding box
			checkBorders(location);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length 
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(
			GeographicPoint from, 
			GeographicPoint to, 
			String roadName,
			String roadType, 
			double length) throws IllegalArgumentException {

		//XXX: First Implementation W2
		//Init new Intersections
		Intersection fromIntersection = new Intersection(from);
		Intersection toIntersection = new Intersection(to);
		
		//Check if start and end exist
		if(!containsVertex(from) || !containsVertex(to))
			throw new IllegalArgumentException("Adding a Road without existing start/end points");
		
		//Instantiate a new Road. 
		//If something goes wrong throws IllegalArgumentException, defined in Road constructor
		Road r = new Road(fromIntersection, toIntersection, roadName, roadType, length);
		//Update road list
		this.updateRoadList(fromIntersection, r);
		//Update number of roads
		this.numRoads++;
		
	}
	
	/**
	 * Represent the Map as a String
	 * @return
	 */
	public String debugToString() {
		String graph = "";
		graph += "Number of Intersections: " + this.getNumVertices() + "\n";
		graph += "Number of Roads: " + this.getNumEdges() + "\n";
		Collection<List<Road>> allRoads = this.map.values();
		Iterator<List<Road>> i = allRoads.iterator();
		while(i.hasNext())
		{
			List<Road> currentRoads = i.next();
			for(Road r : currentRoads)
			{
				graph += r.debugToString() + "\n";
			}
		}
		
		return graph;
	}
	
	private List<GeographicPoint> accept(
			GraphVisitor gV, 
			GeographicPoint start, 
			GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {
		return gV.visit(this, start, goal, nodeSearched);
	}

	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, 
			 					     Consumer<GeographicPoint> nodeSearched)
	{
		//XXX: First Implementation W2
		return this.accept(new BFSGraphVisitor(), start, goal, nodeSearched);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// XXX: First Implementation of the method
		return this.accept(new DjikstraGraphVisitor(), start, goal, nodeSearched);
	}

	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// XXX: First Implementation of the method
		return this.accept(new AStarGraphVisitor(), start, goal, nodeSearched);
	}
	
	/** Find the path from start to goal using A-Star search with Traffic Info
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearchTraffic(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// XXX: First Implementation of the method
		return this.accept(new AStarGraphVisitorTraffic(), start, goal, nodeSearched);
	}
	
	public static void main(String[] args)
	{
		/*
		
		System.out.print("Making a new map...");
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/graders/mod2/map1.txt", theMap);
		System.out.println("DONE.");
		
		System.out.println(theMap.debugToString());
		
		System.out.println(theMap.bfs(new GeographicPoint(0.0, 0.0), new GeographicPoint(6.0, 6.0)));
		System.out.println(theMap.dijkstra(new GeographicPoint(0.0, 0.0), new GeographicPoint(6.0, 6.0)));
		System.out.println(theMap.aStarSearch(new GeographicPoint(0.0, 0.0), new GeographicPoint(6.0, 6.0)));
		
		/*
		// You can use this method for testing.  
		
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		System.out.println(theMap.dijkstra(start,end));
		System.out.println(theMap.aStarSearch(start,end));
		*/
		
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);

		System.out.println(theMap.dijkstra(start,end));
		System.out.println(theMap.aStarSearch(start,end));

		
	}
}
