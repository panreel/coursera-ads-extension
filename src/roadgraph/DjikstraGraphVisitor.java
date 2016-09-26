package roadgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;

public class DjikstraGraphVisitor extends GraphVisitor {

	@Override
	public List<GeographicPoint> visit(
			MapGraph map, 
			GeographicPoint start, 
			GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {
		
		//Init Djikstra Structures
		Map<Intersection, Intersection> paths = new HashMap<Intersection, Intersection>();
		Intersection startIntersection = new Intersection(start);
		Intersection endIntersection = new Intersection(goal);
		
		if(!DjikstraSearch(map, startIntersection, endIntersection, paths, nodeSearched)) return null;
		else return reconstructPath(startIntersection, endIntersection, paths);
		
	}
	
	/**
	 * Core of DjikstraSearch
	 * @param startIntersection - Intersection from which to start
	 * @param endIntersection - Final Intersection
	 * @param paths - Map between visited path
	 * @param nodeSearched - UI Component
	 * @return - True if found, false otherwise
	 */
	private boolean DjikstraSearch(
			MapGraph map, 
			Intersection startIntersection, 
			Intersection endIntersection, 
			Map<Intersection, Intersection> paths,
			Consumer<GeographicPoint> nodeSearched)  {
		
		PriorityQueue<DjikstraIntersection> toInspect = new PriorityQueue<DjikstraIntersection>();
		Set<Intersection> alreadyVisited = new HashSet<Intersection>();
		Map<Intersection, Double> distances = new HashMap<Intersection, Double>();
		boolean DjikstraFinish = false;
		
		//Bootstrap Djikstra with start
		toInspect.add(new DjikstraIntersection(startIntersection, 0));		
		
		//Iterate while Intersections to visit are not finished
		while(!toInspect.isEmpty() && !DjikstraFinish) 
		{
			
			//Extract from Priority Queue
			DjikstraIntersection current = toInspect.poll();
			
			//Check if NOT visited
			if(!alreadyVisited.contains(current)) {
			
				//Add to visited
				alreadyVisited.add(current);
				//Plot to UI
				nodeSearched.accept(current);
				
				//Check if goal
				if(current.equals(endIntersection)) DjikstraFinish = true;
				//Goal not found
				else 
				{
				
					List<Road> nearRoads = map.getRoads(current);
					for(Road nearRoad : nearRoads)
					{
						
						//Get current distance from end of road to goal
						double actualDistance = (distances.containsKey(nearRoad.getEnd()) ? 
								distances.get(nearRoad.getEnd()) : Double.MAX_VALUE);
						
						double newDistance = current.distanceFromStart + nearRoad.getLength();
						
						//If new distance is better than the one calculated before
						if(actualDistance > newDistance)
						{
							//Update data structures
							toInspect.add(new DjikstraIntersection(nearRoad.getEnd(), newDistance));
							distances.put(nearRoad.getEnd(), newDistance);
							paths.put(nearRoad.getEnd(), current);
						}
					}
				}
			}
		}
		
		return DjikstraFinish;
		
	}
	
	class DjikstraIntersection extends Intersection implements Comparable<DjikstraIntersection> {

		private static final long serialVersionUID = 1L;
		private double distanceFromStart;
		
		public DjikstraIntersection(GeographicPoint toWrap) {
			super(toWrap);
			this.distanceFromStart = 0;
		}
		
		public DjikstraIntersection(Intersection e, double newDistance) {
			super(e.x, e.y);
			this.distanceFromStart = newDistance;
		}

		@Override
		public int compareTo(DjikstraIntersection o) {
			if (this.distanceFromStart == o.distanceFromStart) return 0;
			else return ((this.distanceFromStart < o.distanceFromStart)? -1 : 1);
		}
		
	}

}
