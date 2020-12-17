package wumpusWorld;
import java.util.LinkedList;

public class State implements Comparable<State> {
	
	enum TileState {
		UNKNOWN,
		KNOWN,
		SAFE,
		UNSAFE
	}
	
	private GameTile[][] map;
	public int xPos;
	public int yPos;
	private LinkedList<AgentAction> actionsToCurrentState;
	
	private static TileState[][] states;
	
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
		generateTileStates();
	}
	
	State(State s) {
		map = new GameTile[s.map.length][s.map[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (s.map[i][j] != null)
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
	// Actions
	//
	
	// Movement
	public State moveRight() {
		if (!canMoveTo(map[xPos][yPos+1])) {
			return null;
		}
		return new State(this, xPos, yPos+1, AgentAction.moveRight);
	}
	public State moveLeft() {
		if (!canMoveTo(map[xPos][yPos-1]))
			return null;
		return new State(this, xPos, yPos-1, AgentAction.moveLeft);
	}
	public State moveUp() {
		if (!canMoveTo(map[xPos-1][yPos]))
			return null;
		return new State(this, xPos-1, yPos, AgentAction.moveUp);
	}
	public State moveDown() {
		if (!canMoveTo(map[xPos+1][yPos]))
			return null;
		return new State(this, xPos+1, yPos, AgentAction.moveDown);
	}
	
	// Returns false if the agent would die, doesn't know the tile, or it's a wall.
	public boolean canMoveTo(GameTile t) {
		return !(t == null || t.isWall() || t.hasWumpus() || t.hasPit());
	}
	
	// Pickup
	public State pickUp() {
		if (map[xPos][yPos] != null && map[xPos][yPos].hasGlitter()) {
			State result = new State(this, xPos, yPos, AgentAction.pickupSomething);
			result.map[xPos][yPos].setGlitter(false);
			return result;
		}
		return null;
	}
	
	// Shooting
	public State shootRight() {
		int[] wumpusPos = new int[2];
		for (int i = yPos+1; i < map.length; i++) {
			if (map[xPos][i].isWall())
				break;
			if (map[xPos][i].hasWumpus()) {
				wumpusPos[0] = xPos;
				wumpusPos[1] = i;
			}
		}
		if (wumpusPos[0] != 0) {
			State result = new State(this, xPos, yPos, AgentAction.shootArrowEast);
			result.map[wumpusPos[0]][wumpusPos[1]].setWumpus(false);
			return result;
		}
		return this;
	}
	public State shootLeft() {
		int[] wumpusPos = new int[2];
		for (int i = yPos-1; i > 0; i--) {
			if (map[xPos][i].isWall())
				break;
			if (map[xPos][i].hasWumpus()) {
				wumpusPos[0] = xPos;
				wumpusPos[1] = i;
			}
		}
		if (wumpusPos[0] != 0) {
			State result = new State(this, xPos, yPos, AgentAction.shootArrowWest);
			result.map[wumpusPos[0]][wumpusPos[1]].setWumpus(false);
			return result;
		}
		return this;
	}
	public State shootDown() {
		int[] wumpusPos = new int[2];
		for (int i = xPos+1; i < map.length; i++) {
			if (map[i][yPos].isWall())
				break;
			if (map[i][yPos].hasWumpus()) {
				wumpusPos[0] = i;
				wumpusPos[1] = yPos;
			}
		}
		if (wumpusPos[0] != 0) {
			State result = new State(this, xPos, yPos, AgentAction.shootArrowSouth);
			result.map[wumpusPos[0]][wumpusPos[1]].setWumpus(false);
			return result;
		}
		return this;
	}
	public State shootUp() {
		int[] wumpusPos = new int[2];
		for (int i = xPos-1; i > 0; i--) {
			if (map[i][yPos].isWall())
				break;
			if (map[i][yPos].hasWumpus()) {
				wumpusPos[0] = i;
				wumpusPos[1] = yPos;
			}
		}
		if (wumpusPos[0] != 0) {
			State result = new State(this, xPos, yPos, AgentAction.shootArrowNorth);
			result.map[wumpusPos[0]][wumpusPos[1]].setWumpus(false);
			return result;
		}
		return this;
	}
	
	//
	// Goal state checkers
	//
	
	//Returns a state where the Wumpus dies from an arrow shot (if possible)
	public State tryArrowShot() {
		State up = shootUp();
		if (!up.hasWumpus())
			return up;
		State right = shootRight();
		if (!right.hasWumpus())
			return right;
		State down = shootDown();
		if (!down.hasWumpus())
			return down;
		State left = shootLeft();
		if (!left.hasWumpus())
			return left;
		return null;
	}
	
	public int byWumpus() {
		int[] wumpusPos = findWumpus();
		if (wumpusPos != null) {
			if (wumpusPos[0] == xPos) {
				if (wumpusPos[1] == yPos-1)
					return 4;
				if (wumpusPos[1] == yPos+1)
					return 2;
			} else if (wumpusPos[1] == yPos) {
				if (wumpusPos[0] == xPos-1)
					return 1;
				if (wumpusPos[0] == xPos+1)
					return 3;
			}
		}
		return 0;
	}
	
	// Returns direction to move (1, 2, 3, 4 map to up, right, left, down) if by a safe, unknown tile, 0 if not by safe, unknown tile
	public int bySafeTile() {
		if (states[xPos-1][yPos] == TileState.SAFE)
			return 1;
		if (states[xPos][yPos+1] == TileState.SAFE)
			return 2;
		if (states[xPos+1][yPos] == TileState.SAFE)
			return 3;
		if (states[xPos][yPos-1] == TileState.SAFE)
			return 4;
		return 0;
	}
	
	public boolean hasSafeTile() {
		for (int i = 0; i < states.length; i++)
			for (int j = 0; j < states[0].length; j++)
				if (states[i][j] == TileState.SAFE)
					return true;
		return false;
	}
	
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
	
	private boolean hasWumpus() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].hasWumpus())
					return true;
			}
		}
		return false;
	}
	
	//
	// Miscellaneous
	//
	
	public String toHash() {
		String result = String.format("%2d%2d", xPos, yPos);
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == null)
					result += "U";
				else
					result += map[i][j].toHashable();
			}
		}
		return result;
	}
	
	public LinkedList<AgentAction> getActions() {
		return actionsToCurrentState;
	}
	
	private int[] findWumpus() {
		int[] result = null;
		int mostStench = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] != null) continue;
				int stenchCount = 0;
				if (map[i+1][j] != null && map[i+1][j].hasStench())
					stenchCount++;
				if (map[i-1][j] != null && map[i-1][j].hasStench())
					stenchCount++;
				if (map[i][j+1] != null && map[i][j+1].hasStench())
					stenchCount++;
				if (map[i][j-1] != null && map[i][j-1].hasStench())
					stenchCount++;
				
				if (stenchCount > mostStench) {
					mostStench = stenchCount;
					result = new int[2];
					result[0] = i;
					result[1] = j;
				}
			}
		}
		return result;
	}
	
	private void generateTileStates() {
		states = new TileState[map.length][map[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] != null) {
					states[i][j] = TileState.KNOWN;
					if (map[i][j].isWall()) continue;
					if (map[i][j].hasBreeze() || (!map[0][0].heardScream() && map[i][j].hasStench())) {
						if (states[i+1][j] != TileState.KNOWN && states[i+1][j] != TileState.SAFE)
							states[i+1][j] = TileState.UNSAFE;
						if (states[i-1][j] != TileState.KNOWN && states[i-1][j] != TileState.SAFE)
							states[i-1][j] = TileState.UNSAFE;
						if (states[i][j+1] != TileState.KNOWN && states[i][j+1] != TileState.SAFE)
							states[i][j+1] = TileState.UNSAFE;
						if (states[i][j-1] != TileState.KNOWN && states[i][j-1] != TileState.SAFE)
							states[i][j-1] = TileState.UNSAFE;
					} else {
						if (states[i+1][j] != TileState.KNOWN)
							states[i+1][j] = TileState.SAFE;
						if (states[i-1][j] != TileState.KNOWN)
							states[i-1][j] = TileState.SAFE;
						if (states[i][j+1] != TileState.KNOWN)
							states[i][j+1] = TileState.SAFE;
						if (states[i][j-1] != TileState.KNOWN)
							states[i][j-1] = TileState.SAFE;
					}
				} else {
					if (states[i][j] == null)
						states[i][j] = TileState.UNKNOWN;
				}
			}
		}
	}
	
	private void printTileStates() {
		String result = "";
		for (TileState[] l : states) {
			for (TileState t : l) {
				if (t == null) {
					result += "U";
					continue;
				}
				switch (t) {
				case UNKNOWN:
					result += "U";
					break;
				case KNOWN:
					result += " ";
					break;
				case SAFE:
					result += "S";
					break;
				case UNSAFE:
					result += "X";
					break;
				}
			}
			result += '\n';
		}
		System.out.println(result);
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
