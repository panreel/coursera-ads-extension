package roadgraph;

import geography.GeographicPoint;
import week5_apiheuristic.BingTrafficAPIService.MapArea;

/**
 * This class was developed in W2
 * The purpose why I added this definition is related to the possibility
 * to extend the behavior of the standard Map by including, for instance,
 * data about a specific intersection (e.g. avg traffic, #accidents, ...)
 * @author nico
 *
 */
public class Incident extends GeographicPoint {

	private static final long serialVersionUID = 1L;
	private MapArea surrounding;

	public Incident(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	// Wrapping GeographicPoint with Intersection
	public Incident(MapArea m) {
		super(m.getLatCenter(), m.getLonCenter());
		this.surrounding = m;
	}
	
	public String debugToString() {
		return "[" + this.getX() + ", " + this.getY() + "]";
	}

	public MapArea getSurrounding() {
		return surrounding;
	}

}
