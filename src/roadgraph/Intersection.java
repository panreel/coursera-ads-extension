package roadgraph;

import geography.GeographicPoint;

/**
 * This class was developed in W2
 * The purpose why I added this definition is related to the possibility
 * to extend the behavior of the standard Map by including, for instance,
 * data about a specific intersection (e.g. avg traffic, #accidents, ...)
 * @author nico
 *
 */
public class Intersection extends GeographicPoint {

	private static final long serialVersionUID = 1L;

	public Intersection(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	// Wrapping GeographicPoint with Intersection
	public Intersection(GeographicPoint toWrap) {
		super(toWrap.x, toWrap.y);
	}
	
	public String debugToString() {
		return "[" + this.getX() + ", " + this.getY() + "]";
	}

}
