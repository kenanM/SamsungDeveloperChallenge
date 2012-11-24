package com.samsung.comp.football;


public abstract class Action {
	
	protected boolean complete = false; 
	
	public boolean isComplete() {
		return complete;
	}

	/** Used by actors to execute the action. Must also set whether or not the action is completed.
	 * Return true if the action is possible/legal and false if not */
	public abstract boolean executeAction(Player player);
	
}
