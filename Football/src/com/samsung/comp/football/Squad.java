package com.samsung.comp.football;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.samsung.comp.football.Players.Player;

public class Squad {

	int squadID;
	int striker;
	int midfieldA;
	int midfieldB;
	int defender;
	int goalKeeper;

	public Squad(int id, List<Player> players) {
		if (players.size() != 5) {
			Gdx.app.error("Squad", "Size of list for squad isn't 5");
		}
		this.squadID = id;
		striker = players.get(0).getID();
		midfieldA = players.get(1).getID();
		midfieldB = players.get(2).getID();
		defender = players.get(3).getID();
		goalKeeper = players.get(4).getID();
	}

	public Squad(int id, Player[] players) {
		if (players.length != 5) {
			Gdx.app.error("Squad", "Size of list for squad isn't 5");
		}
		this.squadID = id;
		striker = players[0].getID();
		midfieldA = players[1].getID();
		midfieldB = players[2].getID();
		defender = players[3].getID();
		goalKeeper = players[4].getID();
	}

	public Squad(int id, int striker, int midA, int midB, int defender,
			int goalKeeper) {
		this.squadID = id;
		this.striker = striker;
		this.midfieldA = midA;
		this.midfieldB = midB;
		this.defender = defender;
		this.goalKeeper = goalKeeper;
	}

	public int getSquadID() {
		return squadID;
	}

	public void setSquadID(int squadID) {
		this.squadID = squadID;
	}

	public int getStriker() {
		return striker;
	}

	public void setStriker(int striker) {
		this.striker = striker;
	}

	public int getMidfieldA() {
		return midfieldA;
	}

	public void setMidfieldA(int midfieldA) {
		this.midfieldA = midfieldA;
	}

	public int getMidfieldB() {
		return midfieldB;
	}

	public void setMidfieldB(int midfieldB) {
		this.midfieldB = midfieldB;
	}

	public int getDefender() {
		return defender;
	}

	public void setDefender(int defender) {
		this.defender = defender;
	}

	public int getGoalKeeper() {
		return goalKeeper;
	}

	public void setGoalKeeper(int goalKeeper) {
		this.goalKeeper = goalKeeper;
	}


}
