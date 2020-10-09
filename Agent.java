import java.util.HashMap;
import java.util.LinkedList;

public class Agent implements Runnable {
	
	public final int BFS = 101;
	public final int DFS = 102;
	
	private HashMap<String[][], Boolean> nodes;
	private LinkedList<AgentAction> actionQueue;
	private GameObject player;
	private String[][] currentMap;
	
	private Boolean finished;
	private int searchType;
	
	Agent(GameObject p, String[][] map, int st) {
		player = p;
		currentMap = new String[map.length][map[0].length];
		for (int i = 0; i < currentMap.length; i++) {
			for (int j = 0; j < currentMap[i].length; j++) {
				currentMap[i][j] = new String(map[i][j]);
			}
		}
		searchType = st;
		nodes = new HashMap<String[][], Boolean>();
		actionQueue = new LinkedList<AgentAction>();
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
		
	}
	
	public static void main(String[] args) {
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		return;
	}

}
