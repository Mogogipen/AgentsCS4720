package dungeonCrawler;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AgentBrain implements Runnable {
	
	private HashMap<String, Boolean> stateHash;
	private LinkedList<AgentAction> actionQueue;
	private State map;
	
	private Boolean finished;
	private long startTime;
	private long tenSeconds;
	
	AgentBrain(String[][] m) {
		map = new State(m);
		stateHash = new HashMap<String, Boolean>();
		actionQueue = new LinkedList<AgentAction>();
		finished = false;
		long oneSecond = 1000000000;
		tenSeconds = 10 * oneSecond;
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
//		String m = map.toHash();
//		BigInteger stateSpace = BigInteger.ZERO;
//		int gold = 0;
//		for (int i = 0; i < m.length(); i++) {
//			if (m.charAt(i) == '.') {
//				gold++;
//				stateSpace = stateSpace.add(BigInteger.ONE);
//			}
//			if (m.charAt(i) == ' ')
//				stateSpace = stateSpace.add(BigInteger.ONE);
//		}
//		BigInteger goldPower = new BigInteger("2");
//		goldPower = goldPower.pow(gold);
//		stateSpace = stateSpace.multiply(goldPower);
//		if (stateSpace.compareTo(BigInteger.TEN.pow(6)) == 1) {
//			System.out.printf("Possible state space too large: %,d\n", stateSpace);
//			actionQueue.add(AgentAction.declareVictory);
//			return;
//		}
		
		LinkedList<State> searchQueue = new LinkedList<State>();
		State currentState = new State(map);
		while(!finished) {
			// Check if current node is goal state, then break
			if (currentState.isGoalState()) {
				finished = true;
				break;
			}
			
			// Stop if the process is taking longer than 10 seconds.
			long timeTaken = System.nanoTime() - startTime;
			if (timeTaken > tenSeconds) {
				System.out.println("Took too long, space too large.");
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
	
	private void UCS() {
		PriorityQueue<State> searchQueue = new PriorityQueue<State>();
		State currentState = new State(map);
		while(!finished) {
			// Check if current node is goal state, then break
			if (currentState.isGoalState()) {
				finished = true;
				break;
			}
			
			// Stop if the process is taking longer than 10 seconds
			long timeTaken = System.nanoTime() - startTime;
			if (timeTaken > tenSeconds) {
				finished = true;
				System.out.println("Took too long, space too large.");
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
							s.setDistance();
							stateHash.put(hash, true);
							searchQueue.add(s);
						}
					}
				}
			}
			
			// Get the next node in the queue
			currentState = searchQueue.remove();
		}
		actionQueue = currentState.getActions();
		actionQueue.add(AgentAction.declareVictory);
	}

	@Override
	public void run() {
		long start = System.nanoTime();
		startTime = start;
		
		// Run the appropriate algorithm
//		BFS();
//		DFS();
		UCS();
		
		// Print agent metrics to the console
		long stop = System.nanoTime();
		double timeTaken = (double)(stop-start)/1000000000;
		System.out.printf("Time taken: %.3f seconds\n", (timeTaken));
		
		System.out.printf("HashMap size: %,d\n", stateHash.size());
		
		System.out.printf("Actions: %d\n", actionQueue.size()-1);
		
		return;
	}

}
