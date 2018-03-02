import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Car;
import model.Ride;
import model.RideAttractivity;

public class Main {
	
	private static float BONUS_START_ON_TIME_PERCENT = 0.5f;
	private static float MAX_BONUS_FINISH_EARLY_PERCENT = 0.5f;
	
	private static int gridRows;
	private static int gridColumns;
	private static int carsCount;
	private static int ridesCount;
	
	private static int bonusStartOnTime;
	private static long maxBonusEarlyFinish;
	private static long simulationTime;
	
	private static Car[] cars;
	private static Ride[] rides;
	
	public static void main(String[] args) throws IOException {
		if(args.length == 0) {
			System.out.println("Give the filename as an argument!");
			System.exit(1);
		}
		else {
			for(int i=0;i<args.length;i++) {
				System.out.println("Evaluating file: " + args[i]);
				readInput(args[i] + ".in");
				optimize();
				writeOutput(args[i] + ".out");
				System.out.println("");
			}
		}
	}

	private static void readInput(String fileName) throws IOException {
		File file = new File("input/" + fileName);
		Scanner scanner = new Scanner(file);
		
		String line = scanner.nextLine();
        String[] constants = line.split(" ");
        
        gridRows = Integer.valueOf(constants[0]);
        gridColumns = Integer.valueOf(constants[1]);
        carsCount = Integer.valueOf(constants[2]);
        ridesCount = Integer.valueOf(constants[3]);
        bonusStartOnTime = Integer.valueOf(constants[4]);
        simulationTime = Long.valueOf(constants[5]);
        
        cars = new Car[carsCount];
        for(int i=0;i<carsCount;i++) {
        	Car car = new Car(i);
        	cars[i] = car;
        }
        
        rides = new Ride[ridesCount];
        for(int i=0;i<ridesCount;i++) {
        	int startX = scanner.nextInt();
        	int startY = scanner.nextInt();
        	int endX = scanner.nextInt();
        	int endY = scanner.nextInt();
        	long startTime = scanner.nextLong();
        	long endTime = scanner.nextLong();
        	
        	long maxEarlyFinishBonus = endTime - startTime +1; 
        	if(maxEarlyFinishBonus > maxBonusEarlyFinish) {
        		maxBonusEarlyFinish = maxEarlyFinishBonus;
        	}
        	
        	Ride ride = new Ride(i, startX, startY, startTime, endX, endY, endTime);
        	rides[i] = ride;
        }
        
        scanner.close();
        System.out.println("Read completed");     
	}

	private static void optimize() {
		long time = 0;
		while(time < simulationTime) {
			for(int i=0;i<carsCount; i++) {
				Car car = cars[i];
				
				//verify if the car has completed the ride;
				Ride currentRide = car.getCurrentRide();
		
				Boolean isReady = currentRide == null;
				if(!isReady) {
					boolean isGoingAfterRide = car.getIsGoingAfterRide();
					
					if(!isGoingAfterRide) {			
						int rideEndX = currentRide.getEndX();
						int rideEndY = currentRide.getEndY();
						int carX = car.getPosX();
						int carY = car.getPosY();
						
						long currentRideStartTime = car.getStartTimeForRide(currentRide);
						long requiredTimeToFinishRide = Math.abs((rideEndX - carX)) + Math.abs((rideEndY - carY));
						boolean hasBeenFinished = ((currentRideStartTime + requiredTimeToFinishRide) - time) == 0;
						
						if(hasBeenFinished) {
							car.setPosX(rideEndX);
							car.setPosY(rideEndY);
							car.setCurrentRide(null);
							isReady = true;
						}
					}
					else if(isGoingAfterRide) {
						int rideStartX = currentRide.getStartX();
						int rideStartY = currentRide.getStartY();
						int carX = car.getPosX();
						int carY = car.getPosY();
						
						long currentRidePickTime = car.getStartTimeForRide(currentRide);
						long requiredTimeToFinishRide = Math.abs((rideStartX - carX)) + Math.abs((rideStartY - carY));
						boolean hasArrivedToPickUp = ((currentRidePickTime + requiredTimeToFinishRide) - time) == 0;
						
						if(hasArrivedToPickUp) {
							car.setPosX(rideStartX);
							car.setPosY(rideStartY);
							car.updateCurrentRideStartTime(time);
							car.setIsGoingAfterRide(false);
						}
					}
				}
				
				
				// calculate the attrativities of the rides only if the car has no current ride (isReady)
				if(isReady) {
					List<RideAttractivity> rideAttractivities = newRideAttractivities(time);
					
					float totalPondere = 0;
					for(int j=0;j<ridesCount;j++) {
						Ride ride = rides[j];
						
						long timeToPickUp = Math.abs((ride.getStartX() - car.getPosX())) + Math.abs((ride.getStartY() - car.getPosY()));
						long tripTime =  Math.abs((ride.getEndX() - ride.getStartX())) +  Math.abs((ride.getEndY() - ride.getStartY()));
						long rideStartTime = ride.getStartTime();
						long timeWaiting = rideStartTime - (time + timeToPickUp);
						timeWaiting = timeWaiting < 0 ? 0 : timeWaiting;
						long rideEndTime = ride.getEndTime();
						
						
						if((time + timeToPickUp) > rideEndTime || ((time + timeToPickUp + tripTime) > rideEndTime)) {
							rideAttractivities.get(j).setAttractivity(0);
						}
						else {
							long totalTime = timeToPickUp + tripTime + timeWaiting; 
							
							boolean isEarlyStart = (time + timeToPickUp) <= rideStartTime;
							float bonusForStartEarly = isEarlyStart ? totalTime * BONUS_START_ON_TIME_PERCENT : 0; // bonus of 50% of totalTime  
							
							long  earlyFinishTime = rideEndTime - (time + totalTime);
							boolean isEarlyFinish = earlyFinishTime > 0;
							float bonusForEarlyFinish = isEarlyFinish? totalTime * ((earlyFinishTime * MAX_BONUS_FINISH_EARLY_PERCENT)/ maxBonusEarlyFinish) : 0; //bonus of maximum 50% of totalTime (calculated with respect to maxBonusEarlyFinish)
							
							float pondere = 1 / (totalTime - bonusForStartEarly  - bonusForEarlyFinish);
							float attractivity = pondere * rideAttractivities.get(j).getAttractivity();
							rideAttractivities.get(j).setAttractivity(attractivity);
							totalPondere += pondere;
						}
						
					}
					for(int j=0;j<ridesCount;j++) {
						RideAttractivity attr = rideAttractivities.get(j);
						attr.divideAttractivity(totalPondere);
					}
					
					//getting the most attractive ride for the car and assigning it
					RideAttractivity bestAttr = rideAttractivities.get(0);
					for(int j=1; j<rideAttractivities.size(); j++) {
						RideAttractivity attr = rideAttractivities.get(j);
						if(attr.compareTo(bestAttr) < 0) {
							bestAttr = attr;
						}
					}
					if(bestAttr.getAttractivity()!= 0) {
						car.addRide(bestAttr.getRide(), time);
					}
				}
			}
			if(time%1000 == 0) {
				System.out.println("At time: " + time);
			}
			time++;
		}
		System.out.println("Optimize completed");
	}
	
	private static List<RideAttractivity> newRideAttractivities(long currentTime) {
		int totalLeftRides = 0;
		List<RideAttractivity> attractivites = new ArrayList<RideAttractivity>();
		for(int i=0;i<ridesCount;i++) {
			float attractivity = 1;
			if(rides[i].getAssignedCar() != null || rides[i].isInPast(currentTime)) {
				attractivity = 0;
			} 
			else {
				totalLeftRides++;
			}
			RideAttractivity attr = new RideAttractivity(rides[i], attractivity);
			attractivites.add(attr);
		}
		for(RideAttractivity attr : attractivites) {
			attr.divideAttractivity(totalLeftRides);
		}
		return attractivites;
	}
	
	private static void writeOutput(String fileName) throws IOException {
		File file = new File("output/" + fileName);
		BufferedWriter bf = new BufferedWriter(new FileWriter(file));
		for(int i=0;i<carsCount;i++) {
			bf.write(cars[i] + "\n");
		}
		bf.close();
		System.out.println("Write completed");
	}

}
