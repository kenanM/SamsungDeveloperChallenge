package com.samsung.comp.events;

import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Players.Player;

public interface ActionFiredEvent {
	public void setListener(ActionFiredListener observer);
	public void clearListener(ActionFiredListener observer);
	public void actionFired(Player player, Action action);
}
