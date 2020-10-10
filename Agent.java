import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Agent implements Runnable {
	
	private HashMap<String, Boolean> stateHash;
	private LinkedList<AgentAction> actionQueue;
	private State map;
	
	private Boolean finished;
	private Search type;
	
	private long timer;
	
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
		LinkedList<State> searchQueue = new LinkedList<State>();
		State currentState = new State(map);
		while(!finished) {
			// Check if current node is goal state, then break
			if (currentState.isGoalState()) {
				finished = true;
				break;
			}
			
			// Add new nodes
			ArrayList<State> newState = new ArrayList<State>();
			newState.add(currentState.moveDown());
			newState.add(currentState.moveUp());
			newState.add(currentState.moveRight());
			newState.add(currentState.moveLeft());
			newState.add(currentState.pickUp());
			for (State s : newState) {
				if (s != null) {
					String hash = s.toHash();
					if (!stateHash.containsKey(hash) ) {
						stateHash.put(hash, true);
						searchQueue.add(s);
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
//		Check if current node is goal state, then break
		if (currentState.isGoalState()) {
			finished = true;
			actionQueue = currentState.getActions();
			actionQueue.add(AgentAction.declareVictory);
			return;
		}
		
		if (!finished)
			DFS(currentState.moveDown());
		else if (!finished)
			DFS(currentState.moveLeft());
		else if (!finished)
			DFS(currentState.moveRight());
		else if (!finished)
			DFS(currentState.moveUp());
		else if (!finished)
			DFS(currentState.pickUp());
		return;
		
	}

	@Override
	public void run() {
		long start = System.nanoTime();
		if (type == Search.BFS)
			BFS();
		else if (type == Search.DFS)
			DFS();
		else
			System.out.println("Search parameter failure");
		long stop = System.nanoTime();
		double timeTaken = (double)(stop-start)/1000000000;
		finished = true;
		System.out.printf("Time taken: %.3f seconds", (timeTaken));
		return;
	}

}

class State {
	private String[][] map;
	private int xPos;
	private int yPos;
	private LinkedList<AgentAction> actionsToCurrentState;
	
	//
	// Constructors
	//
	
	State(String[][] m) {
		map = new String[m.length][m[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (m[i][j].equals("S")) {
					xPos = i;
					yPos = j;
					map[i][j] = " ";
				} else
					map[i][j] = new String(m[i][j]);
			}
		}
		actionsToCurrentState = new LinkedList<AgentAction>();
	}
	
	State(State s) {
		map = new String[s.map.length][s.map[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new String(s.map[i][j]);
			}
		}
		actionsToCurrentState = new LinkedList<AgentAction>(s.actionsToCurrentState);
		xPos = s.xPos;
		yPos = s.yPos;
	}
	
	private State(State s, int x, int y, AgentAction a) {
		this(s);
		xPos = x;
		yPos = y;
		actionsToCurrentState.add(a);
	}
	
	//
	// Movement
	//
	
	public State moveUp() {
		if (map[xPos][yPos+1].equals("w")) {
			return null;
		}
		return new State(this, xPos, yPos+1, AgentAction.moveRight);
	}
	public State moveDown() {
		if (map[xPos][yPos-1].equals("w"))
			return null;
		return new State(this, xPos, yPos-1, AgentAction.moveLeft);
	}
	public State moveLeft() {
		if (map[xPos-1][yPos].equals("w"))
			return null;
		return new State(this, xPos-1, yPos, AgentAction.moveUp);
	}
	public State moveRight() {
		if (map[xPos+1][yPos].equals("w"))
			return null;
		return new State(this, xPos+1, yPos, AgentAction.moveDown);
	}
	public State pickUp() {
		if (map[xPos][yPos].equals(".")) {
			State result = new State(this, xPos, yPos, AgentAction.pickupSomething);
			result.map[xPos][yPos] = " ";
			return result;
		}
		return null;
	}
	
	//
	// Miscellaneous
	//
	
	public String toHash() {
		String result = ("" + xPos) + yPos;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				result += map[i][j];
			}
		}
		return result;
	}
	
	public boolean isGoalState() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].equals("."))
					return false;
			}
		}
		return true;
	}
	
	public LinkedList<AgentAction> getActions() {
		return actionsToCurrentState;
	}
}

enum Search {
	BFS,
	DFS
}
