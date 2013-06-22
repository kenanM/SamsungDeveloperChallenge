package com.samsung.comp.events;

import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Players.Player;

public interface ActionFiredListener {
	public void onActionFired(Player player, Action action);
}
