import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Agent implements Runnable {
	
	private HashMap<String, Boolean> stateHash;
	private LinkedList<AgentAction> actionQueue;
	private GameObject player;
	private State map;
	
	private Boolean finished;
	private Search type;
	
	Agent(GameObject p, String[][] m, Search t) {
		player = p;
		map = new State(m);
		type = t;
		stateHash = new HashMap<String, Boolean>();
		actionQueue = new LinkedList<AgentAction>();
		finished = false;
	}
	
	public AgentAction nextAction() {
		if (finished) {
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
			// Check if current node is goal state
			currentState.isGoalState();
			
			// Add new nodes
			ArrayList<State> add = new ArrayList<State>();
			add.add(currentState.moveDown());
			add.add(currentState.moveUp());
			add.add(currentState.moveRight());
			add.add(currentState.moveLeft());
			add.add(currentState.pickUp());
			for (State s : add) {
				String hash = s.toHash();
				if (s != null && !stateHash.containsKey(hash) ) {
					stateHash.put(hash, true);
					searchQueue.add(s);
				}
			}
			
			currentState = searchQueue.pop();
		}
	}
	
	private void DFS() {
		
	}
	
	public static void main(String[] args) {
		
	}

	@Override
	public void run() {
		if (type == Search.BFS)
			BFS();
		else if (type == Search.DFS)
			DFS();
		else
			System.out.println("Search parameter failure");
		finished = true;
		return;
	}

}

class State {
	private String[][] map;
	private int xPos;
	private int yPos;
	
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
	}
	
	State(State s) {
		map = new String[s.map.length][s.map[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new String(s.map[i][j]);
			}
		}
		xPos = s.xPos;
		yPos = s.yPos;
	}
	
	private State(State s, int x, int y) {
		this(s);
		xPos = x;
		yPos = y;
	}
	
	//
	// Movement
	//
	
	public State moveUp() {
		if (map[xPos][yPos+1].equals("w")) {
			return null;
		}
		return new State(this, xPos, yPos+1);
	}
	public State moveDown() {
		if (map[xPos][yPos-1].equals("w"))
			return null;
		yPos--;
		return new State(this, xPos, yPos-1);
	}
	public State moveLeft() {
		if (map[xPos-1][yPos].equals("w"))
			return null;
		xPos--;
		return new State(this, xPos-1, yPos);
	}
	public State moveRight() {
		if (map[xPos+1][yPos].equals("w"))
			return null;
		xPos++;
		return new State(this, xPos+1, yPos);
	}
	public State pickUp() {
		if (map[xPos][yPos].equals(".")) {
			State result = new State(this);
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
}

enum Search {
	BFS,
	DFS
}
