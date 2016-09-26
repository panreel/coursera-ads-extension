package roadgraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import geography.GeographicPoint;

public abstract class GraphVisitor {

	public abstract List<GeographicPoint> visit(
			MapGraph map, 
			GeographicPoint start, 
			GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched);
	
	/**
	 * Reconstruct final path from visited roads
	 * @param startIntersection - Intersection from which to start
	 * @param endIntersection - Final Intersection
	 * @param paths - Map between visited path
	 * @return The path from startIntersection to endIntersection
	 */
	 List<GeographicPoint> reconstructPath(
			 Intersection startIntersection, 
			 Intersection endIntersection,
			 Map<Intersection, Intersection> paths) {
		 
		LinkedList<GeographicPoint> finalPath = new LinkedList<GeographicPoint>();
		Intersection current = endIntersection;			
		while(!current.equals(startIntersection))
		{

			finalPath.addFirst(current);
			current = paths.get(current);
		}
		
		finalPath.addFirst(startIntersection);
		return finalPath;	
		
	}
	
}
