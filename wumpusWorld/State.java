package wumpusWorld;
import java.util.LinkedList;

public class State implements Comparable<State> {
	private GameTile[][] map;
	private int xPos;
	private int yPos;
	private LinkedList<AgentAction> actionsToCurrentState;
	
	private int distance;
	private boolean aStar;
	
	//
	// Constructors
	//
	
	State(GameTile[][] m, int x, int y) {
		map = new GameTile[m.length][m[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (m[i][j] == null)
					map[i][j] = null;
				else
					map[i][j] = new GameTile(m[i][j]);
			}
		}
		xPos = x;
		yPos = y;
		actionsToCurrentState = new LinkedList<AgentAction>();
		aStar = false;
	}
	
	State(State s) {
		map = new GameTile[s.map.length][s.map[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new GameTile(s.map[i][j]);
			}
		}
		actionsToCurrentState = new LinkedList<AgentAction>(s.actionsToCurrentState);
		xPos = s.xPos;
		yPos = s.yPos;
		aStar = s.aStar;
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
		if (!canMoveTo(map[xPos][yPos+1])) {
			return null;
		}
		return new State(this, xPos, yPos+1, AgentAction.moveRight);
	}
	public State moveDown() {
		if (!canMoveTo(map[xPos][yPos-1]))
			return null;
		return new State(this, xPos, yPos-1, AgentAction.moveLeft);
	}
	public State moveLeft() {
		if (!canMoveTo(map[xPos-1][yPos]))
			return null;
		return new State(this, xPos-1, yPos, AgentAction.moveUp);
	}
	public State moveRight() {
		if (!canMoveTo(map[xPos+1][yPos]))
			return null;
		return new State(this, xPos+1, yPos, AgentAction.moveDown);
	}
	public State pickUp() {
		if (map[xPos][yPos].hasGlitter()) {
			State result = new State(this, xPos, yPos, AgentAction.pickupSomething);
			result.map[xPos][yPos].setGlitter(false);
			return result;
		}
		return null;
	}
	
	//TODO Add new actions
	
	// Returns false if the agent would die, doesn't know the tile, or it's a wall.
	public boolean canMoveTo(GameTile t) {
		return !(t == null || t.isWall() || t.hasWumpus() || t.hasPit() || !t.hasBeenDiscovered());
	}
	
	//
	// Miscellaneous
	//
	
	public String toHash() {
		String result = String.format("%2d%2d", xPos, yPos);
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				result += map[i][j].toHashable();
			}
		}
		return result;
	}
	
	//TODO Add canShootWumpus()
	
	public boolean hasGold() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].hasGlitter())
					return false;
			}
		}
		return true;
	}
	
	public boolean atExit() {
		if (xPos == map.length-2 && yPos == 1)
			return true;
		return false;
	}
	
	public LinkedList<AgentAction> getActions() {
		return actionsToCurrentState;
	}
	
	//
	// For use with uniform cost search and A* (comparability)
	//
	
	public void aStarComparable() {
		aStar = true;
	}

	@Override
	public int compareTo(State s) {
		if (this.distance > s.distance)
			return 1;
		else if (this.distance < s.distance)
			return -1;
		if (aStar) {
			if (this.actionsToCurrentState.size() > s.actionsToCurrentState.size())
				return 1;
			else if (this.actionsToCurrentState.size() < s.actionsToCurrentState.size())
				return -1;
		}
		return 0;
	}
	
	public void setDistance() {
		distance = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].hasGlitter())
					distance += 2;
				if (i == xPos && j == yPos)
					distance--;
			}
		}
	}
}
