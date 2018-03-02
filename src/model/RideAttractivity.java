package model;

public class RideAttractivity implements Comparable<RideAttractivity>{
	private Ride ride;
	private float attractivity;
	
	 public RideAttractivity(Ride ride, float attractivity) {
		 this.ride = ride;
		this.attractivity = attractivity;
	 }
	 
	 public Ride getRide() {
		return ride;
	}
	 
	 public void divideAttractivity(int divider) {
		 divider = divider == 0 ? 1 : divider;
		 attractivity = attractivity /divider;
	 }
	 
	 public void divideAttractivity(float divider) {
		 divider = divider == 0 ? 1 : divider;
		 attractivity = attractivity /divider;
	 }
	 
	 public void setAttractivity(float attractivity) {
		this.attractivity = attractivity;
	}
	 
	 public float getAttractivity() {
		return attractivity;
	}
	 
	 @Override
	public int compareTo(RideAttractivity o) {
		return Float.compare(o.getAttractivity(), this.getAttractivity());
	}
}
