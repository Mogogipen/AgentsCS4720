package wumpusWorld;

import java.util.LinkedList;
import java.util.Queue;

public class AgentBrain {

	//Don't delete this variable
	private AgentAction nextMove;

	//We reload the brain each time, so this variable needs to be static
	private static int numGamesPlayed = 0;
	private static boolean keyboardPlayOnly = true;

	private int currentNumMoves;

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
	public AgentAction getNextMove(GameTile [][] visableMap) {
		//Possible things to add to your moves
//		nextMove = AgentAction.doNothing;
//		nextMove = AgentAction.moveDown;
//		nextMove = AgentAction.moveUp;
//		nextMove = AgentAction.moveUp;
//		nextMove = AgentAction.moveLeft;
//		nextMove = AgentAction.pickupSomething;
//		nextMove = AgentAction.declareVictory;
//
//		nextMove = AgentAction.shootArrowNorth;
//		nextMove = AgentAction.shootArrowSouth;
//		nextMove = AgentAction.shootArrowEast;
//		nextMove = AgentAction.shootArrowWest;
//		nextMove = AgentAction.quit;


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
		else {
			//This code plays 5 "games" and then quits
			//Just does random things
			if(numGamesPlayed >= 5) {
				return AgentAction.quit;
			}
			else {
				currentNumMoves++;
				if(currentNumMoves < 20) {
					return AgentAction.randomAction();
				}
				else {
					return AgentAction.declareVictory;
				}
			}
		}
	}




}
