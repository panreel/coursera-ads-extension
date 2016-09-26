package roadgraph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.http.client.ClientProtocolException;

import geography.GeographicPoint;
import week5_apiheuristic.BingTrafficAPIService;
import week5_apiheuristic.BingTrafficAPIService.MapArea;

public class AStarGraphVisitorTraffic extends GraphVisitor {

	@Override
	public List<GeographicPoint> visit(
			MapGraph map, 
			GeographicPoint start, 
			GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {

		//Init AStar Structures
		Map<Intersection, Intersection> paths = new HashMap<Intersection, Intersection>();
		Intersection startIntersection = new Intersection(start);
		Intersection endIntersection = new Intersection(goal);
		
		//Traffic management additional structs
		BingTrafficAPIService BingTraffic;
		List<MapArea> incidents = new ArrayList<MapArea>();
		
		try {
			
			BingTraffic = new BingTrafficAPIService();
			System.out.println("Bing Traffic API set up!");
			//Call Bing Traffic API for traffic info and adds MapAreas to incidents list
			incidents.addAll(BingTraffic.getTrafficFlowData(map.getBoundingBox()));
			incidents.stream().forEach(System.out::println);
			System.out.println("Incidents found: " + incidents.size());
			
			//Pass Incident to UI for plotting
			incidents.stream().map(m -> new Incident(m)).forEach(nodeSearched);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!AStarSearch(map, startIntersection, endIntersection, paths, nodeSearched, incidents)) return null;
		else return reconstructPath(startIntersection, endIntersection, paths);
		
	}
	
	/**
	 * Check if an intersection is in some jammed areas
	 * @param intersection intersection to check
	 * @param incidents list
	 * @return true if intersection is in a jammed area
	 */
	private int checkCongestioned(Intersection intersection, List<MapArea> incidents) {
		for(MapArea incident : incidents) {
			if(incident.contains(intersection.getX(), intersection.getY()))
				return incident.getSeverity();
		}
		return -1;
	}
	
	/**
	 * Core of AStarSearch
	 * @param startIntersection - Intersection from which to start
	 * @param endIntersection - Final Intersection
	 * @param paths - Map between visited path
	 * @param nodeSearched - UI Component
	 * @return - True if found, false otherwise
	 */
	private boolean AStarSearch(
			MapGraph map, 
			Intersection startIntersection, 
			Intersection endIntersection, 
			Map<Intersection, Intersection> paths,
			Consumer<GeographicPoint> nodeSearched,
			List<MapArea> incidents)  {
		
		PriorityQueue<AStarIntersectionTraffic> toInspect = new PriorityQueue<AStarIntersectionTraffic>();
		Set<Intersection> alreadyVisited = new HashSet<Intersection>();
		Map<Intersection, Double> distances = new HashMap<Intersection, Double>();
		boolean AStarFinish = false;
		
		//Bootstrap AStart with start
		toInspect.add(new AStarIntersectionTraffic(startIntersection, 0, startIntersection.distance(endIntersection), -1));		
		
		//Iterate while Intersections to visit are not finished
		while(!toInspect.isEmpty() && !AStarFinish) 
		{
			//Extract from Priority Queue
			AStarIntersectionTraffic current = toInspect.poll();
			
			//Check if NOT visited
			if(!alreadyVisited.contains(current)) {
			
				//Add to visited
				alreadyVisited.add(current);
				//Plot to UI
				nodeSearched.accept(current);
				
				//Check if goal
				if(current.equals(endIntersection)) AStarFinish = true;
				//Goal not found
				else 
				{
				
					List<Road> nearRoads = map.getRoads(current);
					for(Road nearRoad : nearRoads)
					{
						
						//Get current distance from startIntersection to end of nearRoad
						double actualDistance = (distances.containsKey(nearRoad.getEnd()) ? 
								distances.get(nearRoad.getEnd()) : Double.MAX_VALUE);
						int actualJam = checkCongestioned(current, incidents);
						
						double newDistance = current.distanceFromStart + nearRoad.getLength();
						int newJam = checkCongestioned(nearRoad.getEnd(), incidents);
								
						//If new distance is better than the one calculated before
						if(actualDistance > newDistance)
						{
							//Update data structures by adding also jam information
							toInspect.add(
									new AStarIntersectionTraffic(
											nearRoad.getEnd(), 
											newDistance, 
											nearRoad.getEnd().distance(endIntersection),
											checkCongestioned(nearRoad.getEnd(), incidents)));
							distances.put(nearRoad.getEnd(), newDistance);
							paths.put(nearRoad.getEnd(), current);
						}
					}	
				}	
			}
		}
		
		return AStarFinish;
		
	}
	
	class AStarIntersectionTraffic extends Intersection implements Comparable<AStarIntersectionTraffic> {

		private static final long serialVersionUID = 1L;
		private double distanceFromStart;
		private double heuristicToGoal;
		private int congestedAreaSeverity;
		
		public AStarIntersectionTraffic(GeographicPoint toWrap) {
			super(toWrap);
			this.distanceFromStart = 0;
			this.heuristicToGoal = 0;
			this.congestedAreaSeverity = -1;
		}
		
		public AStarIntersectionTraffic(Intersection e, double newDistance, double newHeuristic, int severity) {
			super(e.x, e.y);
			this.distanceFromStart = newDistance;
			this.heuristicToGoal = newHeuristic;
			this.congestedAreaSeverity = severity;
		}

		@Override
		public int compareTo(AStarIntersectionTraffic o) {
			
			double o1OverallScore = this.distanceFromStart + this.heuristicToGoal;
			double o2OverallScore = o.distanceFromStart + o.heuristicToGoal;
			
			//Compare if in congested areas and prefer the one in non congested one
			if(this.congestedAreaSeverity > 0 && o.congestedAreaSeverity > 0 
					&& this.congestedAreaSeverity != o.congestedAreaSeverity) {
				return (this.congestedAreaSeverity < o.congestedAreaSeverity) ? -1 : 1;
			} 
			else if (this.congestedAreaSeverity > 0 && o.congestedAreaSeverity <= 0) {
				return 1;
			} 
			else if (this.congestedAreaSeverity <= 0 && o.congestedAreaSeverity > 0) {
				return -1;
			} 
			//If both in congested or non congested areas evaluate heuristics
			else if (o1OverallScore == o2OverallScore) return 0;
			else return ((o1OverallScore < o2OverallScore)? -1 : 1);
		}
		
	}

}
