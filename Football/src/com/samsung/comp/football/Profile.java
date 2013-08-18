package com.samsung.comp.football;

public class Profile {

	protected int profileID;
	protected int funds;

	public Profile(int id, int funds) {
		this.profileID = id;
		this.funds = funds;
	}

	public int getProfileID() {
		return profileID;
	}

	public int getFunds() {
		return funds;
	}
}
