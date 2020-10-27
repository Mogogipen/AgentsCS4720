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
		
		hillClimbingSearch(cities);
//		states = hillClimbingSearch(cities);
		
		//This returns quite a few
		for(int i = 0; i < 10; i++) {
			//Create a copy
			City [] tmpArray = new City[cities.length];
			for(int j = 0; j < cities.length; j++) {
				tmpArray[j] = cities[j];
			}
			//shuffle and add it to the array
			List<City> tmp = Arrays.asList(tmpArray);
			Collections.shuffle(tmp);
			states.add(tmp.toArray(tmpArray));
		}
		return states;
	}
	
	private static ArrayList<City []> hillClimbingSearch(City[] cities) {
		ArrayList<City []> states = new ArrayList<City []>();
		
		boolean isBest = false;
		while(!isBest) {
			ArrayList<City []> checkStates = new ArrayList<City []>();
			for (int i = 0; i < cities.length; i++) {
				checkStates.add(swapCities(i, cities));
			}
			
			isBest = true;
		}
		
		return states;
	}
	
	// Returns a new City[] with the index swapped with the index that follows
	private static City[] swapCities(int index, City[] cities) {
		City[] result = new City[cities.length];
		for (int i = 0; i < cities.length; i++) {
			result[i] = cities[i];
		}
		if (index == cities.length - 1) {
			City temp = cities[0];
			cities[0] = cities[index];
			cities[index] = temp;
		} else {
			City temp = cities[index];
			cities[index] = cities[index + 1];
			cities[index + 1] = temp;
		}
		return result;
	}
	
	private static int getRouteDistance(City[] cities) {
		int result = 0;
		for (int i = 0; i < cities.length; i++) {
			if (i == cities.length)
				result += cities[i].distance(cities[0]);
			else
				result += cities[i].distance(cities[i+1]);
		}
		return result;
	}

}
