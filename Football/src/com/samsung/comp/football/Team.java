package com.samsung.comp.football;

public class Team {

	private int teamID = 0;
	private int profileID = 0;
	private String teamName;
	private int difficulty;
	private int wins;
	private int losses;
	private int draws;

	public Team(int teamID, int profileID, String name, int difficulty,
			int wins, int losses, int draws) {
		this(teamID, profileID, name, difficulty);
		this.wins = wins;
		this.losses = losses;
		this.draws = draws;
	}

	public Team(int teamID, int profileID, String name, int difficulty) {
		this.teamID = teamID;
		this.profileID = profileID;
		this.teamName = name;
		this.difficulty = difficulty;
		this.wins = 0;
		this.losses = 0;
		this.draws = 0;
	}

	public int getTeamID() {
		return teamID;
	}

	public int getProfileID() {
		return profileID;
	}

	public String getTeamName() {
		return teamName;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public int getWins() {
		return wins;
	}

	public int getLosses() {
		return losses;
	}

	public int getDraws() {
		return draws;
	}
}
