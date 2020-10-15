import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Agent implements Runnable {
	
	private HashMap<String, Boolean> stateHash;
	private LinkedList<AgentAction> actionQueue;
	private State map;
	
	private Boolean finished;
	private Search type;
	
	Agent(String[][] m, Search t) {
		map = new State(m);
		type = t;
		stateHash = new HashMap<String, Boolean>();
		actionQueue = new LinkedList<AgentAction>();
		finished = false;
	}
	
	public AgentAction nextAction() {
		if (finished && !actionQueue.isEmpty()) {
			return actionQueue.pop();
		}
		return AgentAction.doNothing;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	private void BFS() {
		
		// If the state space is too large, cancel
		String m = map.toHash();
		long stateSpace = 0;
		int gold = 0;
		for (int i = 0; i < m.length(); i++) {
			if (m.charAt(i) == '.') {
				gold++;
				stateSpace++;
			}
			if (m.charAt(i) == ' ')
				stateSpace++;
		}
		stateSpace *= (long)Math.pow(2, gold);
		if (stateSpace > 1000000 || gold > 30) {
			System.out.printf("State space too large: %,d\n", stateSpace);
			return;
		}
		
		LinkedList<State> searchQueue = new LinkedList<State>();
		State currentState = new State(map);
		while(!finished) {
			// Check if current node is goal state, then break
			if (currentState.isGoalState()) {
				finished = true;
				break;
			}
			
			// Add new nodes to queue
			State pickUpTest = currentState.pickUp();
			if (pickUpTest != null) {
				stateHash.put(pickUpTest.toHash(), true);
				searchQueue.add(pickUpTest);
			} else {
				ArrayList<State> newState = new ArrayList<State>();
				newState.add(currentState.moveDown());
				newState.add(currentState.moveUp());
				newState.add(currentState.moveRight());
				newState.add(currentState.moveLeft());
				for (State s : newState) {
					if (s != null) {
						String hash = s.toHash();
						if (!stateHash.containsKey(hash) ) {
							stateHash.put(hash, true);
							searchQueue.add(s);
						}
					}
				}
			}
			
			// Get the next node in the queue
			currentState = searchQueue.pop();
		}
		actionQueue = currentState.getActions();
		actionQueue.add(AgentAction.declareVictory);
	}
	
	private void DFS() {
		State currentState = new State(map);
		DFS(currentState);
	}
	
	private void DFS(State currentState) {
		// Hash
		String hash = currentState.toHash();
		stateHash.put(hash, true);
		
		// Check if current node is goal state, then return
		if (currentState.isGoalState()) {
			finished = true;
			actionQueue = currentState.getActions();
			actionQueue.add(AgentAction.declareVictory);
			return;
		}
		
		// Go through the next possible movements (prioritizing pickup)
		State nextState;
		
		if (!finished) {
			nextState = currentState.pickUp();
			if (nextState != null && !stateHash.containsKey(nextState.toHash()))
				DFS(nextState);
		} if (!finished) {
			nextState = currentState.moveDown();
			if (nextState != null && !stateHash.containsKey(nextState.toHash()))
				DFS(nextState);
		} if (!finished) {
			nextState = currentState.moveUp();
			if (nextState != null && !stateHash.containsKey(nextState.toHash()))
				DFS(nextState);
		} if (!finished) {
			nextState = currentState.moveLeft();
			if (nextState != null && !stateHash.containsKey(nextState.toHash()))
				DFS(nextState);
		} if (!finished) {
			nextState = currentState.moveRight();
			if (nextState != null && !stateHash.containsKey(nextState.toHash()))
				DFS(nextState);
		}
		return;
	}

	@Override
	public void run() {
		long start = System.nanoTime();
		
		// Run the appropriate algorithm
		if (type == null)
			return;
		if (type == Search.BFS)
			BFS();
		else if (type == Search.DFS)
			DFS();
		else
			System.out.println("Search parameter failure");
		
		// Print time taken and hash size to the console
		long stop = System.nanoTime();
		double timeTaken = (double)(stop-start)/1000000000;
		System.out.printf("Time taken: %.3f seconds\n", (timeTaken));
		
		System.out.printf("HashMap size: %,d\n", stateHash.size());
		
		return;
	}

}

enum Search {
	BFS,
	DFS
}
