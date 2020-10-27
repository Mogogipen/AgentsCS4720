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
		states = hillClimbingSearch(cities);
		
		// Search for the best using 
		
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
		
		// Print comparison
		System.out.printf("Starting route distance: %d\nFinal route distance: %d\n\n", getRouteDistance(cities), getRouteDistance(bestRoute));
		
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
