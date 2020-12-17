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
	private boolean explored = false;
	private boolean explored2 = false;
	private boolean shotArrow = false;
	private boolean hasGold = false; //TODO Temp value, change to false in the future
	private boolean foundExit = false;
	private boolean inProgress = false;
	
	private final int KILLWUMPUS = 1;
	private final int FINDEXIT = 2;
	private final int FINDGOLD = 3;
	private final int FINDSAFE = 4;

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
			
			// Runs old method if the whole map is visible
			if (allVisible(visibleMap))
				return allVisibleMethod(visibleMap, pos);
			
			// Otherwise, use the new method
			AgentAction a = normalMethod(visibleMap, pos);
			if (a != AgentAction.doNothing)
				currentNumMoves++;
			return a;
		}
	}
	
	private AgentAction normalMethod(GameTile[][] visibleMap, int[] pos) {
		
		if (!inProgress) {
			
			if (visibleMap[0][0].heardScream() && !explored2) {
				explored = false;
				explored2 = true;
			}
			
			// Check if current spot has gold
			if (visibleMap[pos[0]][pos[1]].hasGlitter()) {
				hasGold = true;
				return AgentAction.pickupSomething;
			}
			
			// Explore the whole map for the gold
			if (!explored && !hasGold) {
				BFS(visibleMap, pos[0], pos[1], FINDSAFE);
				inProgress = true;
			}
			
			// If the gold isn't found by the end of exploration: find and shoot the wumpus
			else if (explored && !shotArrow && !hasGold) {
				BFS(visibleMap, pos[0], pos[1], KILLWUMPUS);
				inProgress = true;
			}
			
			else {
				BFS(visibleMap, pos[0], pos[1], FINDEXIT);
				inProgress = true;
			}
			
		}
		if (!actionQueue.isEmpty()) {
			currentNumMoves++;
			return actionQueue.pop();
		} else
			inProgress = false;
		
		return AgentAction.doNothing;
	}
	
	private AgentAction allVisibleMethod(GameTile[][] visibleMap, int[] pos) {
		
		if (!inProgress) {
			// First assignment: find the exit
			if (shotArrow && hasGold && !foundExit) {
				BFS(visibleMap, pos[0], pos[1], FINDEXIT);
				inProgress = true;
			}
			
			// Third assignment: find the gold
			if (shotArrow && !hasGold) {
				BFS(visibleMap, pos[0], pos[1], FINDGOLD);
				inProgress = true;
			}
			
			// Second assignment: kill the Wumpus
			if (!shotArrow) {
				BFS(visibleMap, pos[0], pos[1], KILLWUMPUS);
				inProgress = true;
			}

		}
			
		if (!actionQueue.isEmpty()) {
			currentNumMoves++;
			return actionQueue.pop();
		} else
			inProgress = false;
		
		return AgentAction.doNothing; 
	}

	// Using breadth first search, find the shortest path to the given goal
	private void BFS(GameTile[][] map, int xPos, int yPos, int find) {
		LinkedList<State> searchQueue = new LinkedList<State>();
		State currentState = new State(map, xPos, yPos);
		
		HashMap<String, Boolean> stateHash = new HashMap<String, Boolean>();
		
		boolean goalReached = false;
		
		while(!goalReached) {
			
			// Check if current node is goal state, then break
			if (FINDEXIT == find) {
				if (currentState.atExit()) {
					actionQueue = currentState.getActions();
					
					// After 10 games, the agent quits rather than declares victory
					if (numGamesPlayed >= 20)
						actionQueue.add(AgentAction.quit);
					else actionQueue.add(AgentAction.declareVictory);
					
					foundExit = true;
					goalReached = true;
					break;
				}
			} else if (KILLWUMPUS == find) {
				int dir = currentState.byWumpus();
				if (dir != 0) {
					actionQueue  = currentState.getActions();
					goalReached = true;
					shotArrow = true;
					if (dir == 1)
						actionQueue.add(AgentAction.shootArrowNorth);
					else if (dir == 2)
						actionQueue.add(AgentAction.shootArrowEast);
					else if (dir == 3)
						actionQueue.add(AgentAction.shootArrowSouth);
					else if (dir == 4)
						actionQueue.add(AgentAction.shootArrowWest);
					else
						System.out.println("Invalid shoot direction (byWumpus check)");
					break;
				}
			} else if (FINDGOLD == find) {
				if (currentState.hasGold()) {
					actionQueue = currentState.getActions();
					hasGold = true;
					goalReached = true;
					break;
				}

			} else if (FINDSAFE == find) {
				// Case of state not having a safe tile
				if (!currentState.hasSafeTile()) {
					actionQueue = new LinkedList<AgentAction>();
					actionQueue.add(AgentAction.doNothing);
					goalReached = true;
					explored = true;
					break;
				}
				int dir = currentState.bySafeTile();
				if (dir > 0) {
					actionQueue = currentState.getActions();
					goalReached = true;
					if (dir == 1)
						actionQueue.add(AgentAction.moveUp);
					else if (dir == 2)
						actionQueue.add(AgentAction.moveRight);
					else if (dir == 3)
						actionQueue.add(AgentAction.moveDown);
					else if (dir == 4)
						actionQueue.add(AgentAction.moveLeft);
					else
						System.err.println("Invalid movement value (safeTile check)");
					break;
				}

			} else {
				System.err.println("Invalid search goal");
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
				if (!shotArrow) shotArrow = true;
				else {
					actionQueue = new LinkedList<AgentAction>();
					actionQueue.add(AgentAction.declareVictory);
					hasGold = true;
					foundExit = true;
				}
				return;
			}
			
			// Get the next node in the queue
			currentState = searchQueue.pop();
		}
	}
	
	private int[] findPlayer(GameTile[][] visibleMap) {
		int[] result = new int[2];
		for (int i = 0; i < visibleMap.length; i++) {
			for (int j = 0; j < visibleMap[0].length; j++) {
				if (visibleMap[i][j] != null && visibleMap[i][j].hasPlayer()) {
					result[0] = i;
					result[1] = j;
				}
			}
		}
		return result;
	}

	private boolean allVisible(GameTile[][] visibleMap) {
		for (GameTile[] l : visibleMap)
			for (GameTile t : l)
				if (t == null)
					return false;
		return true;
	}

}