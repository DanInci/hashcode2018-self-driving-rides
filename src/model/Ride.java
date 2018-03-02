package model;

public class Ride {
	private int rideId;
	private int startX;
	private int startY;
	private long startTime;
	private int endX;
	private int endY;
	private long endTime;
	private Car assignedCar;
	
	public Ride(int rideId, int startX, int startY, long startTime, int endX, int endY, long endTime) {
		this.rideId = rideId;
		this.startX = startX;
		this.startY = startY;
		this.startTime = startTime;
		this.endX = endX;
		this.endY = endY;
		this.endTime = endTime;
	}
	
	public int getRideId() {
		return rideId;
	}
	
	public void setAssignedCar(Car assignedCar) {
		this.assignedCar = assignedCar;
	}
	
	public Car getAssignedCar() {
		return assignedCar;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public int getStartX() {
		return startX;
	}
	
	public int getStartY() {
		return startY;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public int getEndX() {
		return endX;
	}
	
	public int getEndY() {
		return endY;
	}
	
	public Boolean isInPast(long currentTime) {
		if(currentTime > endTime) {
			return true;
		}
		return false;
	}
	
}
