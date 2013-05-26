package com.samsung.comp.events;

import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Players.Player;

public interface ObservableActionFired {
	public void subscribe(ActionFiredObserver observer);
	public void unsubscribe(ActionFiredObserver observer);
	public void notifyActionFired(Player player, Action action);
}
