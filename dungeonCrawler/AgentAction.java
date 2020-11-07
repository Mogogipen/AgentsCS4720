package dungeonCrawler;

public class AgentAction {
	public static final AgentAction moveLeft = new AgentAction(true);
	public static final AgentAction moveRight = new AgentAction(true);
	public static final AgentAction moveUp = new AgentAction(true);
	public static final AgentAction moveDown = new AgentAction(true);
	
	public static final AgentAction pickupSomething = new AgentAction(true);
	public static final AgentAction declareVictory = new AgentAction(false);
	
	public static final AgentAction doNothing = new AgentAction(false);
	
	
	private boolean isAnAction;
	
	public AgentAction(boolean isAnAction) {
		this.isAnAction = isAnAction;
	}
	
	public boolean isAnAction() {
		return isAnAction;
	}
	
	//Temp
	@Override
	public String toString() {
		if(this == moveLeft)
			return "left";
		if(this == moveRight)
			return "right";
		if(this == moveUp)
			return "up";
		if(this == moveDown)
			return "down";
		if(this == pickupSomething)
			return "pickup";
		if(this == declareVictory)
			return "victory";
		if(this == doNothing)
			return "noop";
		return "fail";
	}
}
