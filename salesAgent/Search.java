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
		
//		hillClimbingSearch(cities);
		
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
		ArrayList<City []> checkStates = new ArrayList<City []>();
		
		boolean isBest = false;
		while(!isBest) {
			isBest = true;
		}
		
		return states;
	}
	
	private static int getTotalDistance(City[] cities) {
		int result = 0;
		for (int i = 0; i < cities.length; i++) {
			if (i == cities.length)
				result += distance(cities[i], cities[0]);
			else
				result += distance(cities[i], cities[i+1]);
		}
		return result;
	}
	
	private static int distance(City a, City b) {
		int result = 0;
		int deltaX = Math.abs(a.getX() - b.getX());
		int deltaY = Math.abs(a.getY() - b.getY());
		result = (int)Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
		return result;
	}

}
