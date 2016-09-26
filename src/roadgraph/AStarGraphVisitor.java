package roadgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;

public class AStarGraphVisitor extends GraphVisitor {

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
		
		if(!AStarSearch(map, startIntersection, endIntersection, paths, nodeSearched)) return null;
		else return reconstructPath(startIntersection, endIntersection, paths);
		
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
			Consumer<GeographicPoint> nodeSearched)  {
		
		PriorityQueue<AStarIntersection> toInspect = new PriorityQueue<AStarIntersection>();
		Set<Intersection> alreadyVisited = new HashSet<Intersection>();
		Map<Intersection, Double> distances = new HashMap<Intersection, Double>();
		boolean AStarFinish = false;
		
		//Bootstrap AStart with start
		toInspect.add(new AStarIntersection(startIntersection, 0, startIntersection.distance(endIntersection)));		
		
		//Iterate while Intersections to visit are not finished
		while(!toInspect.isEmpty() && !AStarFinish) 
		{
			//Extract from Priority Queue
			AStarIntersection current = toInspect.poll();
			
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
						
						double newDistance = current.distanceFromStart + nearRoad.getLength();
								
						//If new distance is better than the one calculated before
						if(actualDistance > newDistance)
						{
							//Update data structures
							toInspect.add(new AStarIntersection(nearRoad.getEnd(), newDistance, nearRoad.getEnd().distance(endIntersection)));
							distances.put(nearRoad.getEnd(), newDistance);
							paths.put(nearRoad.getEnd(), current);
						}
					}	
				}	
			}
		}
		
		return AStarFinish;
		
	}
	
	class AStarIntersection extends Intersection implements Comparable<AStarIntersection> {

		private static final long serialVersionUID = 1L;
		private double distanceFromStart;
		private double heuristicToGoal;
		
		public AStarIntersection(GeographicPoint toWrap) {
			super(toWrap);
			this.distanceFromStart = 0;
			this.heuristicToGoal = 0;
		}
		
		public AStarIntersection(Intersection e, double newDistance, double newHeuristic) {
			super(e.x, e.y);
			this.distanceFromStart = newDistance;
			this.heuristicToGoal = newHeuristic;
		}

		@Override
		public int compareTo(AStarIntersection o) {
			double o1OverallScore = this.distanceFromStart + this.heuristicToGoal;
			double o2OverallScore = o.distanceFromStart + o.heuristicToGoal;
			if (o1OverallScore == o2OverallScore) return 0;
			else return ((o1OverallScore < o2OverallScore)? -1 : 1);
		}
		
	}

}
