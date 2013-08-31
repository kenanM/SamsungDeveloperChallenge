package com.samsung.comp.football.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Holds a database connection and a set of accessible table managers to
 * interact with the database.
 * 
 */
public class PlayerDataSource {

	private SQLiteDatabase database;
	private PlayerDatabaseHelper helper;

	private ProfilesTableManager profilesTableManager;
	private TeamsTableManager teamsTableManager;
	private PlayersTableManager playersTableManager;
	private SquadsTableManager squadsTableManager;

	protected static final int AI_PROFILE_ID = 1;
	protected static final int STORE_TEAM_ID = 2;

	public PlayerDataSource(Context context) {
		helper = new PlayerDatabaseHelper(context);
		open();
	}

	public void open() throws SQLException {
		database = helper.getWritableDatabase();

		profilesTableManager = new ProfilesTableManager(database);
		// ProfilesTableManager.dropAndRecreate(database);

		teamsTableManager = new TeamsTableManager(database);
		// TeamsTableManager.dropAndRecreate(database);

		playersTableManager = new PlayersTableManager(database);
		// PlayersTableManager.dropAndRecreate(database);

		squadsTableManager = new SquadsTableManager(database);
		// SquadsTableManager.dropAndRecreate(database);

	}

	public void close() {
		helper.close();
	}

	public ProfilesTableManager getProfilesTableManager() {
		return profilesTableManager;
	}

	public TeamsTableManager getTeamsTableManager() {
		return teamsTableManager;
	}

	public PlayersTableManager getPlayersTableManager() {
		return playersTableManager;
	}

	public SquadsTableManager getSquadsTableManager() {
		return squadsTableManager;
	}

}
