package com.samsung.comp.events;

import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Players.Player;


public interface BallOwnerSetListener {
	public void onBallOwnerSet(Ball ball, Player newOwner);
}
