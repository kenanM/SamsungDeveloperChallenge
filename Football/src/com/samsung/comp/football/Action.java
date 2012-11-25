package com.samsung.comp.football;

public abstract class Action {

	protected boolean complete = false;

	public boolean isComplete() {
		return complete;
	}

	public abstract void executeNextStep();

}
