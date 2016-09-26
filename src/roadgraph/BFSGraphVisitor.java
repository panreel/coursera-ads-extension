package roadgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;

public class BFSGraphVisitor extends GraphVisitor {
	
	@Override
	public List<GeographicPoint> visit(
			MapGraph map, 
			GeographicPoint start, 
			GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {

		//Init BFS Structures
		Map<Intersection, Intersection> paths = new HashMap<Intersection, Intersection>();
		Intersection startIntersection = new Intersection(start);
		Intersection endIntersection = new Intersection(goal);
				
		if(!BFSSearch(map, startIntersection, endIntersection, paths, nodeSearched)) return null;
		else return reconstructPath(startIntersection, endIntersection, paths);
		
	}
	
	/**
	 * Core of BFS Search
	 * @param startIntersection - Intersection from which to start
	 * @param endIntersection - Final Intersection
	 * @param paths - Map between visited path
	 * @param nodeSearched - UI Component
	 * @return - True if found, false otherwise
	 */
	private boolean BFSSearch(
			MapGraph map, 
			Intersection startIntersection, 
			Intersection endIntersection, 
			Map<Intersection, Intersection> paths,
			Consumer<GeographicPoint> nodeSearched)  {
		
		Queue<Intersection> toInspect = new LinkedList<Intersection>();
		Set<Intersection> alreadyVisited = new HashSet<Intersection>();
		boolean BFSFinish = false;
		
		//Bootstrap BFS with start
		alreadyVisited.add(startIntersection);
		toInspect.add(startIntersection);		
		
		//Iterate while Intersections to visit are not finished
		while(!toInspect.isEmpty() && !BFSFinish) 
		{
			Intersection current = toInspect.poll();
			
			//Plot to UI
			nodeSearched.accept(current);
			
			//Check if goal
			if(current.equals(endIntersection)) BFSFinish = true;
			//Goal not found
			else 
			{
				//Extract roads from around
				List<Road> nearRoads = map.getRoads(current);
				for(Road nearRoad : nearRoads)
				{
					//If end point of departing road already visited
					if(!alreadyVisited.contains(nearRoad.getEnd()))
					{
						//Update data structures
						alreadyVisited.add(nearRoad.getEnd());
						toInspect.add(nearRoad.getEnd());
						paths.put(nearRoad.getEnd(), current);
					}
				}
			}
		}
		
		return BFSFinish;
		
	}

}
