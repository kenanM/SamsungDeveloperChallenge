package com.samsung.comp.events;

import com.samsung.comp.football.Actions.Action;

public interface ActionObserver extends Observer {
	public void onActionFired(Action action);
}
