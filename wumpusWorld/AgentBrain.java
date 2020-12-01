package wumpusWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class AgentBrain {

	//Don't delete this variable
	private AgentAction nextMove;

	//We reload the brain each time, so this variable needs to be static
	private static int numGamesPlayed = 0;
	private static boolean keyboardPlayOnly = false;

	private int currentNumMoves;
	
	//My Variables
	private LinkedList<AgentAction> actionQueue;
	private boolean hasGold = true; //TODO Temp value, change to dynamic in the future
	private boolean shotArrow = false;
	private boolean foundExit = false;
	
	private final int FINDWUMPUS = 1;
	private final int FINDEXIT = 2;
	private final int FINDGOLD = 3;

	public AgentBrain() {
		nextMove = null;

		numGamesPlayed++;

		currentNumMoves = 0;
	}

	public void setNextMove(AgentAction m) {
		if(nextMove != null) {
			System.out.println("Trouble adding move, only allowed to add 1 at a time");
		}
		else {
			nextMove = m;
		}
	}
	
	//For wumpus world, we do one move at a time
	public AgentAction getNextMove(GameTile [][] visibleMap) {

		//Ideally you would remove all this code, but I left it in so the keylistener would work
		if(keyboardPlayOnly) {
			if(nextMove == null) {
				return AgentAction.doNothing;
			}
			else {
				AgentAction tmp = nextMove;
				nextMove = null;
				return tmp;
			}

		}
		
		// My code runs if the keyboard play is set to false
		else {
			// Find the player each time
			int[] pos = new int[2];
			pos = findPlayer(visibleMap);
			
			// Second assignment: find the wumpus
			if (!shotArrow) {
				BFS(visibleMap, pos[0], pos[1], FINDWUMPUS);
			}
			
			// First assignment: find the exit (hasGold is set to true)
			if (shotArrow && hasGold && !foundExit)
				BFS(visibleMap, pos[0], pos[1], FINDEXIT);
			
			if (!actionQueue.isEmpty()) {
				currentNumMoves++;
				return actionQueue.pop();
			}
			
			return AgentAction.doNothing; 
		}
	}

	// Using breadth first search, find the shortest path to the exit
	// TODO Change name, and add goal parameter
	private void BFS(GameTile[][] map, int xPos, int yPos, int find) {
		LinkedList<State> searchQueue = new LinkedList<State>();
		State currentState = new State(map, xPos, yPos);
		
		HashMap<String, Boolean> stateHash = new HashMap<String, Boolean>();
		
		boolean goalReached = false;
		
		while(!goalReached) {
			// Check if current node is goal state, then break
			switch (find) {
				case FINDEXIT:
					if (currentState.atExit()) {
						foundExit = true;
						goalReached = true;
						break;
					}
				case FINDWUMPUS:
					if (currentState.canShootWumpus()) {
						shotArrow = true;
						goalReached = true;
						break;
					}
				case FINDGOLD:
					if (currentState.hasGold()) {
						hasGold = true;
						goalReached = true;
						break;
					}
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
			
			// If the search queue is empty, there is no solution
			if (searchQueue.isEmpty()) {
				actionQueue = new LinkedList<AgentAction>();
				actionQueue.add(AgentAction.declareVictory);
				foundExit = true;
				return;
			}
			
			// Get the next node in the queue
			currentState = searchQueue.pop();
		}
		actionQueue = currentState.getActions();
		
		// After 10 games, the agent quits rather than declares victory
		if (numGamesPlayed >= 10)
			actionQueue.add(AgentAction.quit);
		else actionQueue.add(AgentAction.declareVictory);
	}
	
	private int[] findPlayer(GameTile[][] visibleMap) {
		int[] result = new int[2];
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[0].length; j++) {
				if (visibleMap[i][j].hasPlayer()) {
					result[0] = i;
					result[1] = j;
				}
			}
		}
		return result;
	}


}
