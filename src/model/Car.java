package model;

import java.util.HashMap;
import java.util.Set;

public class Car {
	private int carId;
	private int posX;
	private int posY;
	private HashMap<Ride, Long> assginedRides = new HashMap<Ride, Long>();
	private Ride currentRide;
	private Boolean isGoingAfterRide = false;
	
	public Car(int carId) {
		this.carId = carId;
	}
	
	public int getCarId() {
		return carId;
	}
	
	public void setPosX(int posX) {
		this.posX = posX;
	}
	
	public void setPosY(int posY) {
		this.posY = posY;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public Set<Ride> getAssginedRides() {
		return assginedRides.keySet();
	}
	
	public void addRide(Ride ride, Long currentTime) {
		assginedRides.put(ride, currentTime);
		currentRide = ride;
		isGoingAfterRide = true;
		ride.setAssignedCar(this);
	}
	
	public Ride getCurrentRide() {
		return currentRide;
	}
	
	public void setCurrentRide(Ride currentRide) {
		this.currentRide = currentRide;
	}
	
	public void updateCurrentRideStartTime(Long time) {
		if(currentRide!=null) {
			assginedRides.remove(currentRide);
			assginedRides.put(currentRide, time);
		}
	}
	
	public Long getStartTimeForRide(Ride ride) {
		return assginedRides.get(ride);
	}
	
	public Boolean getIsGoingAfterRide() {
		return isGoingAfterRide;
	}
	
	public void setIsGoingAfterRide(Boolean isGoingAfterRide) {
		this.isGoingAfterRide = isGoingAfterRide;
	}
	
	@Override
	public String toString() {
		String desc = assginedRides.size() + " ";
		for(Ride ride : assginedRides.keySet()) {
			desc += ride.getRideId() + " ";
		}
		return desc;
	}

}
