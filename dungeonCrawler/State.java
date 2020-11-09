package dungeonCrawler;
import java.util.LinkedList;

public class State implements Comparable<State> {
	private String[][] map;
	private int xPos;
	private int yPos;
	private LinkedList<AgentAction> actionsToCurrentState;
	
	private int distance;
	
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
		String result = String.format("%2d%2d", xPos, yPos);
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
	
	//
	// For use with uniform cost search (comparability)

	@Override
	public int compareTo(State s) {
		if (this.distance > s.distance)
			return -1;
		else if (this.distance < s.distance)
			return 1;
		return 0;
	}
	
	public void setDistance() {
		distance = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].equals("."))
					distance += 2;
				if (i == xPos && j == yPos)
					distance--;
			}
		}
	}
}
