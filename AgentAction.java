
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
}
