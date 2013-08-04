package com.samsung.comp.football.data;

import static com.samsung.comp.football.data.PlayerDatabaseHelper.GOALIE_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.ID_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.PLAYER_NAME_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.PLAYER_TABLE_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.PURCHASED_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.RUN_SPEED_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.SAVING_SKILL_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.SHOOT_SPEED_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.TACKLE_PREVENTION_SKILL_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.TACKLE_SKILL_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.TEAM_ID_COLUMN_NAME;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;

public class PlayerDataSource {

	private SQLiteDatabase database;
	private PlayerDatabaseHelper helper;

	private static final String[] allColumns = { ID_COLUMN_NAME,
			PLAYER_NAME_COLUMN_NAME, PURCHASED_COLUMN_NAME,
			SHOOT_SPEED_COLUMN_NAME, RUN_SPEED_COLUMN_NAME,
			TACKLE_SKILL_COLUMN_NAME, TACKLE_PREVENTION_SKILL_COLUMN_NAME,
			SAVING_SKILL_COLUMN_NAME, TEAM_ID_COLUMN_NAME, GOALIE_COLUMN_NAME };

	public PlayerDataSource(Context context) {
		helper = new PlayerDatabaseHelper(context);
		open();
	}

	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	public List<Player> getPlayers(int teamID) {
		Cursor cursor = database.query(PLAYER_TABLE_NAME, null,
				TEAM_ID_COLUMN_NAME + "=? and " + GOALIE_COLUMN_NAME + " =0",
				new String[] { Integer.toString(teamID) }, null, null, null);
		cursor.moveToFirst();
		LinkedList<Player> result = new LinkedList<Player>();
		do {
			result.add(cursorToPlayer(cursor));
		} while (cursor.moveToNext() == true);

		Log.v("db", "Found a total of " + result.size() + " players for team "
				+ teamID);
		return result;
	}

	public Goalie getGoalie(int teamID) {
		Cursor cursor = database.query(PLAYER_TABLE_NAME, allColumns,
				TEAM_ID_COLUMN_NAME + "=? and " + GOALIE_COLUMN_NAME + " =1",
				new String[] { Integer.toString(teamID) }, null, null, null);
		cursor.moveToFirst();
		return (Goalie) cursorToPlayer(cursor);
	}

	/**
	 * Extract a Player object from a cursor pointing at a row in the player
	 * table
	 */
	public static Player cursorToPlayer(Cursor cursor) {
		String name = cursor.getString(cursor
				.getColumnIndex(PLAYER_NAME_COLUMN_NAME));
		boolean purchased = (cursor.getInt(cursor
				.getColumnIndex(PURCHASED_COLUMN_NAME)) > 0);
		float shootSpeed = cursor.getFloat(cursor
				.getColumnIndex(SHOOT_SPEED_COLUMN_NAME));
		float runSpeed = cursor.getFloat(cursor
				.getColumnIndex(RUN_SPEED_COLUMN_NAME));
		float tackleSkill = cursor.getFloat(cursor
				.getColumnIndex(TACKLE_SKILL_COLUMN_NAME));
		float tacklePrevention = cursor.getFloat(cursor
				.getColumnIndex(TACKLE_PREVENTION_SKILL_COLUMN_NAME));
		float savingSkill = cursor.getFloat(cursor
				.getColumnIndex(SAVING_SKILL_COLUMN_NAME));
		int teamId = cursor.getInt(cursor.getColumnIndex(TEAM_ID_COLUMN_NAME));
		int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));

		boolean goalie = cursor.getInt(cursor
				.getColumnIndex(GOALIE_COLUMN_NAME)) > 0;

		if (goalie) {
			return new Goalie(id, name, purchased, shootSpeed, runSpeed,
					tackleSkill, tacklePrevention, savingSkill, teamId);
		} else {
			return new Player(id, name, purchased, shootSpeed, runSpeed,
					tackleSkill, tacklePrevention, savingSkill, teamId);
		}
	}
}
