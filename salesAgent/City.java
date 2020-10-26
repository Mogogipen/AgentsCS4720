package salesAgent;

public class City {

	private int x;
	private int y;
	
	public City() {
		this.x = (int)(Math.random()*DrawIt.WIDTH*.9+DrawIt.WIDTH*.05);
		this.y = (int)(Math.random()*DrawIt.HEIGHT*.9+DrawIt.HEIGHT*.05);
	}
	
	public double distance (City c) {
		double aSquared = Math.abs(x-c.x);
		aSquared *= aSquared;
		double bSquared = Math.abs(y-c.y);
		bSquared *= bSquared;
		return Math.sqrt(aSquared+bSquared);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
	
}
