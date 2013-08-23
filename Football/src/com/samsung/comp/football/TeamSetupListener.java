package com.samsung.comp.football;

import com.samsung.comp.football.Players.Player;

public interface TeamSetupListener {
	public void onSelectedPlayerChanged(TeamSetupScreen screen, Player player,
			int index);
	public void onStartButtonPressed(TeamSetupScreen screen);
}
