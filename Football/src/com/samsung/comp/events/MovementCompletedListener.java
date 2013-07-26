package com.samsung.comp.events;

import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Players.Player;

public interface MovementCompletedListener {
	public void onMovementCompleted(Player player, Action nextAction);
}
