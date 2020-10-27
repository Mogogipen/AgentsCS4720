package salesAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Search {
	
	public static ArrayList<City []> search(City [] cities) {
		ArrayList<City []> states = new ArrayList<City []>();
		
		//This returns a single result
//		City [] c = new City[2];
//		c[0] = new City();
//		c[1] = new City();
//		states.add(c);
//		return states;
		
		// Search for the best using hill climbing
//		states = hillClimbingSearch(cities);
		
		// Search for the best using simulated annealing
		states = simulatedAnnealingSearch(cities);
		
		//This returns quite a few
//		for(int i = 0; i < 10; i++) {
//			//Create a copy
//			City [] tmpArray = new City[cities.length];
//			for(int j = 0; j < cities.length; j++) {
//				tmpArray[j] = cities[j];
//			}
//			//shuffle and add it to the array
//			List<City> tmp = Arrays.asList(tmpArray);
//			Collections.shuffle(tmp);
//			states.add(tmp.toArray(tmpArray));
//		}
		
		// Print comparison
		System.out.printf("Starting route distance: %d\nFinal route distance: %d\n\n", getRouteDistance(cities), getRouteDistance(states.get(states.size()-1)));
		return states;
	}
	
	private static ArrayList<City []> hillClimbingSearch(City[] cities) {
		ArrayList<City []> states = new ArrayList<City []>();
		states.add(cities);
		
		boolean isBest = false;
		City[] bestRoute = cities;
		while(!isBest) {
			// Generate new routes
			ArrayList<City []> checkStates = new ArrayList<City []>();
			for (int i = 0; i < bestRoute.length; i++) {
				checkStates.add(swapCities(i, bestRoute));
			}
			
			// Check for better route
			boolean betterFound = false;
			int bestDistance = getRouteDistance(bestRoute);
			for (City[] ca : checkStates) {
				int testDistance = getRouteDistance(ca);
				if (testDistance < bestDistance) {
					betterFound = true;
					bestDistance = testDistance;
					bestRoute = ca;
				}
			}
			
			// If no better route is found, quit the loop; otherwise add the next best and continue
			if (!betterFound)
				isBest = true;
			else
				states.add(bestRoute);
		}
		
		return states;
	}
	
	private static ArrayList<City []> simulatedAnnealingSearch(City[] cities) {
		ArrayList<City []> states = new ArrayList<City []>();
		states.add(cities);
		
		boolean isBest = false;
		City[] currentRoute = cities;
		int currentDistance = getRouteDistance(currentRoute);
		double temperature = 1.0;
		double deltaT = (1.0/cities.length)/2;
		
		while (!isBest) {
			if (temperature < 0.01) {
				states.addAll(hillClimbingSearch(currentRoute));
				isBest = true;
			}
			
			else {
				// Generate next state
				boolean nextGenerated = false;
				while(!nextGenerated) {
//					System.out.printf("%.4f\n", temperature);
					// Two random indices in the route
					int i1 = (int)(Math.random()*cities.length);
					int i2 = (int)(Math.random()*cities.length);
					if (i1 == i2);  // If the indices are identical, generate new ones
					else {
						// Swap the cities and switch if better, or if the temperature is just right;
						City[] testRoute = swapCities(i1, i2, currentRoute);
						int testDistance = getRouteDistance(testRoute);
						if (testDistance < currentDistance || temperature > Math.random()) {
							currentRoute = testRoute;
							currentDistance = testDistance;
							nextGenerated = true;
						}
					}
				}
				
				// Decrease temperature and add the new route
				temperature -= deltaT;
				states.add(currentRoute);
			}
		}
		
		return states;
	}
	
	// Returns a new City[] with the index swapped with the index that follows
	private static City[] swapCities(int index, City[] cities) {
		City[] result = new City[cities.length];
		// Copy the array
		for (int i = 0; i < cities.length; i++) {
			result[i] = cities[i];
		}
		
		// Swap the given index with the following index
		int next = index + 1;
		if (next == cities.length)
			next = 0;
		City temp = result[index];
		result[index] = result[next];
		result[next] = temp;
		return result;
	}
	// Returns a new City[] with the elements swapped between the 2 given indices
	private static City[] swapCities(int index1, int index2, City[] cities) {
		City[] result = new City[cities.length];
		// Copy the array
		for (int i = 0; i < cities.length; i++)
			result[i] = cities[i];
		
		// Swap the given indices
		City temp = result[index1];
		result[index1] = result[index2];
		result[index2] = temp;
		return result;
	}
	
	private static int getRouteDistance(City[] cities) {
		int result = 0;
		for (int i = 0; i < cities.length; i++) {
			if (i == cities.length - 1)
				result += cities[i].distance(cities[0]);
			else
				result += cities[i].distance(cities[i+1]);
		}
		return result;
	}

}
