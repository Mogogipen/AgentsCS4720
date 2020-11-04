package salesAgent;

import java.util.ArrayList;
import java.util.Arrays;

public class GeneticRoute implements Comparable<GeneticRoute>{
	
	private int routeDistance;
	private City[] route;
	
	GeneticRoute(City[] r) {
		setRoute(r);
	}
	
	public void setRoute(City[] r) {
		route = r;
		routeDistance = Search.getRouteDistance(route);
	}
	
	public int getRouteDistance() {
		return routeDistance;
	}
	
	public City[] getRoute() {
		return route;
	}
	
	public ArrayList<GeneticRoute> breed(GeneticRoute partner, int count) {
		ArrayList<GeneticRoute> children = new ArrayList<GeneticRoute>();
		
		// Generate children
		for (int i = 0; i < count; i++) {
			
			// Get a random subset of this one
			int start = (int)(Math.random()*route.length);
			int size = (int)(Math.random()*(route.length - 1) + 1);
			int end = (start + size) % route.length;
			City[] baby = new City[route.length];
			int itr = start;
			while (itr != end) {
				baby[itr] = route[itr];
				if (++itr == route.length)
					itr = 0;
			}
			
			// Put the remaining cities from the partner into the subset
			for (int j = 0; itr != start; j++) {
				boolean babyHas = false;
				for (int k = 0; k < baby.length; k++) {
					if (partner.route[j] == baby[k]) {
						babyHas = true;
						break;
					}
				}
				if (!babyHas) {
					baby[itr] = partner.route[j];
					if (++itr == route.length)
						itr = 0;
				}
			}

			// Mutate the baby
			boolean mutated = false;
			while (!mutated) {
				int i1 = (int)(Math.random()*route.length);
				int i2 = (int)(Math.random()*route.length);
				if (i1 == i2) continue;
				
				City tmp = baby[i1];
				baby[i1] = baby[i2];
				baby[i2] = tmp;
				mutated = true;
			}
			
			
			// Make a full child and add it to the array
			children.add(new GeneticRoute(baby));
		}
		
		return children;
	}

	@Override
	// If this is a faster (shorter) return a 1
	public int compareTo(GeneticRoute arg0) {
		if(routeDistance < arg0.routeDistance)
			return 1;
		else if (routeDistance == arg0.routeDistance)
			return 0;
		return -1;
	}

}
