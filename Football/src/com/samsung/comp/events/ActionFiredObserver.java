package com.samsung.comp.events;

import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Players.Player;

public interface ActionFiredObserver extends Observer {
	public void onActionFired(Player player, Action action);
}
