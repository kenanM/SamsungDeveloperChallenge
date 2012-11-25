package com.samsung.comp.football.Actions;

import java.util.Random;

import com.samsung.comp.football.Player;

public class TestMove extends Action {

	Player player;

	Random random = new Random();
	int count = 76;
	int tempX = 0;
	int tempY = 0;

	public TestMove(Player player) {
		this.player = player;
	}

	@Override
	public void executeNextStep() {
		count++;
		if (count > 75) {
			count = 0;
			tempX = random.nextInt(3) - 1;
			tempY = random.nextInt(3) - 1;
		}
		player.x += tempX;
		player.y += tempY;
	}

}
