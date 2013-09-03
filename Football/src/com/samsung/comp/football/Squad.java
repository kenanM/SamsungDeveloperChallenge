package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.precisionfootball.R;

public class Squad {

	int squadID;
	Player striker;
	Player midfieldA;
	Player midfieldB;
	Player defender;
	Player goalKeeper;

	public Squad(int id, List<Player> players) {
		if (players.size() != 5) {
			Gdx.app.error("Squad", "Size of list for squad isn't 5");
		}
		this.squadID = id;
		striker = players.get(0);
		midfieldA = players.get(1);
		midfieldB = players.get(2);
		defender = players.get(3);
		goalKeeper = players.get(4);
	}

	public Squad(int id, Player[] players) {
		if (players.length != 5) {
			Gdx.app.error("Squad", "Size of list for squad isn't 5");
		}
		this.squadID = id;
		striker = players[0];
		midfieldA = players[1];
		midfieldB = players[2];
		defender = players[3];
		goalKeeper = players[4];
	}

	public Squad(int id, Player striker, Player midA, Player midB,
			Player defender, Player goalKeeper) {
		this.squadID = id;
		this.striker = striker;
		this.midfieldA = midA;
		this.midfieldB = midB;
		this.defender = defender;
		this.goalKeeper = goalKeeper;
	}

	/**
	 * Returns a list of all players, farthest forward first.
	 * 
	 * @return
	 */
	public List<Player> getAllPlayers() {
		List<Player> players = new ArrayList<Player>();
		players.add(this.striker);
		players.add(this.midfieldA);
		players.add(this.midfieldB);
		players.add(this.defender);
		players.add(this.goalKeeper);
		return players;
	}

	public int getSquadID() {
		return squadID;
	}

	public void setSquadID(int squadID) {
		this.squadID = squadID;
	}

	public Player getStriker() {
		return striker;
	}

	public void setStriker(Player striker) {
		this.striker = striker;
	}

	public Player getMidfieldA() {
		return midfieldA;
	}

	public void setMidfieldA(Player midfieldA) {
		this.midfieldA = midfieldA;
	}

	public Player getMidfieldB() {
		return midfieldB;
	}

	public void setMidfieldB(Player midfieldB) {
		this.midfieldB = midfieldB;
	}

	public Player getDefender() {
		return defender;
	}

	public void setDefender(Player defender) {
		this.defender = defender;
	}

	public Player getGoalKeeper() {
		return goalKeeper;
	}

	public void setGoalKeeper(Player goalKeeper) {
		this.goalKeeper = goalKeeper;
	}

}
