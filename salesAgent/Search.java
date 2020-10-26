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

}
