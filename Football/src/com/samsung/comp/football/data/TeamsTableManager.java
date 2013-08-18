package com.samsung.comp.football.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.comp.football.Team;

/**
 * This class creates and updates the players database and is heavily inspired
 * by: http://www.vogella.com/articles/AndroidSQLite/article.html
 */
public class TeamsTableManager {

	protected static final String TEAMS_TABLE_NAME = "teams";
	protected static final String TEAM_ID_COLUMN_NAME = "_id";
	protected static final String TEAM_NAME_COLUMN_NAME = "team_name";
	protected static final String PROFILE_COLUMN_NAME = "profile_id";
	protected static final String TEAM_DIFFICULTY_COLUMN_NAME = "team_difficulty";
	protected static final String WIN_COUNT_COLUMN_NAME = "win_count";
	protected static final String LOSS_COUNT_COLUMN_NAME = "loss_count";
	protected static final String DRAW_COUNT_COLUMN_NAME = "draw_count";

	private static final String CREATE_TEAMS_TABLE = "create table "
			+ TEAMS_TABLE_NAME + "(" + TEAM_ID_COLUMN_NAME
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TEAM_NAME_COLUMN_NAME
			+ " text not null, " + PROFILE_COLUMN_NAME + " integer not null, "
			+ TEAM_DIFFICULTY_COLUMN_NAME + " integer not null, "
			+ WIN_COUNT_COLUMN_NAME + " int not null, "
			+ LOSS_COUNT_COLUMN_NAME + " int not null" + DRAW_COUNT_COLUMN_NAME
			+ " int not null" + "FOREIGN KEY(" + PROFILE_COLUMN_NAME
			+ ") REFERENCES "
			+ ProfileTableManager.PROFILES_TABLE_NAME + "("
			+ ProfileTableManager.PROFILE_ID_COLUMN_NAME + ")" + ");";

	// static class hack
	private TeamsTableManager() {
	}

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TEAMS_TABLE);
		addDefaultTeams(database);
	}


	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(TeamsTableManager.class.getName(),
				"Upgrading teams table from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TEAMS_TABLE_NAME);
		onCreate(db);
	}

	private static void addDefaultTeams(SQLiteDatabase database) {

		Team[] teams = { new Team(1, 0, "The Misfits", 1),
				new Team(2, 0, "Team Brunel", 1),
				new Team(3, 0, "Team Null", 1),
				new Team(3, 0, "The All Rounders", 1) };
		for (Team team : teams) {
			insertTeam(database, team);
		}
	}

	private static void insertTeam(SQLiteDatabase database, Team team) {
		Log.v(PlayerDataSource.class.toString(),
				"adding team: " + team.toString());

		ContentValues values = new ContentValues();
		values.put(TEAM_NAME_COLUMN_NAME, team.getTeamName());
		values.put(PROFILE_COLUMN_NAME, team.getProfileID());
		values.put(TEAM_DIFFICULTY_COLUMN_NAME, team.getDifficulty());
		values.put(WIN_COUNT_COLUMN_NAME, team.getWins());
		values.put(LOSS_COUNT_COLUMN_NAME, team.getLosses());
		values.put(DRAW_COUNT_COLUMN_NAME, team.getDraws());

		Log.v("db", "team_id: " + team.getTeamID());
		Log.v("db", "profile_id: " + team.getProfileID());
		database.insert(TEAMS_TABLE_NAME, null, values);
	}

	private static Team cursorToTeam(Cursor cursor) {

		int teamID = cursor.getInt(cursor.getColumnIndex(TEAM_ID_COLUMN_NAME));
		int profileID = cursor.getInt(cursor
				.getColumnIndex(PROFILE_COLUMN_NAME));
		String teamName = cursor.getString(cursor
				.getColumnIndex(TEAM_NAME_COLUMN_NAME));
		int teamDifficulty = cursor.getInt(cursor
				.getColumnIndex(TEAM_DIFFICULTY_COLUMN_NAME));
		int winCount = cursor.getInt(cursor
				.getColumnIndex(WIN_COUNT_COLUMN_NAME));
		int lossCount = cursor.getInt(cursor
				.getColumnIndex(LOSS_COUNT_COLUMN_NAME));
		int drawCount = cursor.getInt(cursor
				.getColumnIndex(DRAW_COUNT_COLUMN_NAME));

		return new Team(teamID, profileID, teamName, teamDifficulty, winCount,
				lossCount, drawCount);
	}

	public static Team getTeam(SQLiteDatabase database, int teamID) {
		Cursor cursor = database.query(TEAMS_TABLE_NAME, null,
				TEAM_ID_COLUMN_NAME + "=? ",
				new String[] { Integer.toString(teamID) }, null, null, null);
		cursor.moveToFirst();
		return cursorToTeam(cursor);
	}

}

