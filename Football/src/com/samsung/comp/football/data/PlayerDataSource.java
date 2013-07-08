package com.samsung.comp.football.data;

import static com.samsung.comp.football.data.PlayerDatabaseHelper.PLAYER_TABLE_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.ID_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.PLAYER_NAME_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.*;

import java.util.List;

import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlayerDataSource {

	private SQLiteDatabase database;
	private PlayerDatabaseHelper helper;

	private static final String[] allColumns = { ID_COLUMN_NAME,
			PLAYER_NAME_COLUMN_NAME, PURCHASED_COLUMN_NAME,
			SHOOT_SPEED_COLUMN_NAME, RUN_SPEED_COLUMN_NAME,
			TACKLE_SKILL_COLUMN_NAME, TACKLE_PREVENTION_SKILL_COLUMN_NAME,
			SAVING_SKILL_COLUMN_NAME };

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

	public void addPlayers(List<Player> players) {
		for (Player player : players) {
			addPlayer(player);
		}
	}

	private void addPlayer(Player player) {
		Log.v(PlayerDataSource.class.toString(),
				"adding player: " + player.toString());
		ContentValues values = new ContentValues();
		values.put(PLAYER_NAME_COLUMN_NAME, player.getName());
		values.put(PURCHASED_COLUMN_NAME, player.isPurchased() ? 1 : 0);
		values.put(SHOOT_SPEED_COLUMN_NAME, player.getShootSpeed());
		values.put(RUN_SPEED_COLUMN_NAME, player.getRunSpeed());
		values.put(TACKLE_SKILL_COLUMN_NAME, player.getTackleSkill());
		values.put(TACKLE_PREVENTION_SKILL_COLUMN_NAME,
				player.getTacklePreventionSkill());
		values.put(SAVING_SKILL_COLUMN_NAME, player.getSavingSkill());

		database.insert(PLAYER_TABLE_NAME, null, values);
	}

	/** Returns a cursor of a select all statement */
	public Cursor getCursor() {
		return database.query(PLAYER_TABLE_NAME, allColumns, null, null, null,
				null, null);
	}

	/**
	 * Extract a Player object from a cursor pointing at a row in the player
	 * table
	 */
	public static Player cursorToBook(Cursor cursor) {
		String name = cursor.getString(cursor
				.getColumnIndex(PLAYER_NAME_COLUMN_NAME));
		boolean purchased = (cursor.getInt(cursor
				.getColumnIndex(PURCHASED_COLUMN_NAME)) != 0);
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

		return new Player(0, 0, null, shootSpeed,
				runSpeed, tackleSkill, tacklePrevention, savingSkill);
	}
}
