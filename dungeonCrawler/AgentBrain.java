package dungeonCrawler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class AgentBrain {

	private Queue<AgentAction> nextMoves;
	
	private HashMap<String, Boolean> stateHash;
	private boolean finished;
	private State map;

	public AgentBrain() {
		nextMoves = new LinkedList<AgentAction>();
	}
	
	public void addNextMove(AgentAction nextMove) {
		this.nextMoves.add(nextMove);
	}

	public void clearAllMoves() {
		nextMoves = new LinkedList<AgentAction>();
	}

	public AgentAction getNextMove() {
		if(nextMoves.isEmpty()) {
			return AgentAction.doNothing;
		}
		return nextMoves.remove();
	}
	
	
	public void search(String [][] theMap) {
		finished = false;
		map = new State(theMap);
		
		BFS();
//		DFS();
	}
	
	private void BFS() {
		
		// If the state space is too large, cancel
		String m = map.toHash();
		BigInteger stateSpace = BigInteger.ZERO;
		int gold = 0;
		for (int i = 0; i < m.length(); i++) {
			if (m.charAt(i) == '.') {
				gold++;
				stateSpace = stateSpace.add(BigInteger.ONE);
			}
			if (m.charAt(i) == ' ')
				stateSpace = stateSpace.add(BigInteger.ONE);
		}
		BigInteger goldPower = new BigInteger("2");
		goldPower = goldPower.pow(gold);
		stateSpace = stateSpace.multiply(goldPower);
		if (stateSpace.compareTo(BigInteger.TEN.pow(6)) == 1) {
			System.out.printf("Possible state space too large: %,d\n", stateSpace);
			nextMoves.add(AgentAction.declareVictory);
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
		nextMoves = currentState.getActions();
		nextMoves.add(AgentAction.declareVictory);
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
			nextMoves = currentState.getActions();
			nextMoves.add(AgentAction.declareVictory);
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
	
	
}
