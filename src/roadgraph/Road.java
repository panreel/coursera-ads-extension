package roadgraph;

public class Road {
	
	private Intersection start;
	private Intersection end;
	private String name;
	private String type;
	private double length;
	
	// Constructor
	public Road(Intersection start, Intersection end, 
			String name, String type, double length) throws IllegalArgumentException {
		
		if(start == null || end == null || name == null || type == null || length <= 0)
			throw new IllegalArgumentException("No valid argument passed");
		
		this.start = start;
		this.end = end;
		this.name = name;
		this.type = type;
		this.length = length;
		
	}
	
	// Getter: return road start Geographic Point
	public Intersection getStart() {
		return this.start;
	}
	
	// Getter: return road end Geographic Point
	public Intersection getEnd() {
		return this.end;
	}
	
	// Getter: return road name
	public String getName() {
		return this.name;
	}
	
	// Getter: return road type
	public String getType() {
		return this.type;
	}
	
	// Getter: return road length
	public double getLength() {
		return this.length;
	}
	
	public String debugToString() {
		return this.getStart().debugToString() + 
				" - (" + this.getType() + ", " + this.getLength() + "km) -> " + 
				this.getEnd().debugToString();
	}

}
