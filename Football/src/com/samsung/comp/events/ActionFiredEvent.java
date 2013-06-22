package com.samsung.comp.events;

import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Players.Player;

public interface ActionFiredEvent {
	public void subscribe(ActionFiredListener observer);
	public void unsubscribe(ActionFiredListener observer);
	public void actionFired(Player player, Action action);
}
