package com.samsung.comp.football.data;

import static com.samsung.comp.football.data.PlayerDatabaseHelper.*;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.TEAM_ID_COLUMN_NAME;
import java.util.LinkedList;
import java.util.List;

import com.samsung.comp.football.Players.Goalie;
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
			SAVING_SKILL_COLUMN_NAME, TEAM_ID_COLUMN_NAME };

	public PlayerDataSource(Context context) {
		Log.v("db", "Instantiating playerDataSource");
		helper = new PlayerDatabaseHelper(context);
		Log.v("db", "Instantiated playerDataSource");
		open();
		Log.v("db", "opened at line 35");
	}

	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	public List<Player> getTeam(int teamID){
		Cursor cursor = database.query(PLAYER_TABLE_NAME, allColumns, "TEAM_ID_COLUMN_NAME ="+teamID,null, null, null, null);
		cursor.moveToFirst();
		LinkedList<Player> result = new LinkedList<Player>();
		while(cursor.isAfterLast() == false){
			result.add(cursorToPlayer(cursor));
		}
		return result;
	}
	public Goalie getGoalie(int teamID){
		Cursor cursor = database.query(PLAYER_TABLE_NAME, allColumns, null,new String[]{TEAM_ID_COLUMN_NAME + "=" + teamID, GOALIE_COLUMN_NAME + "=1"}, null, null, null);
		cursor.moveToFirst();
		return (Goalie) cursorToPlayer(cursor);
	}

	/**
	 * Extract a Player object from a cursor pointing at a row in the player
	 * table
	 */
	public static Player cursorToPlayer(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
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
		boolean goalie = cursor.getInt(cursor.getColumnIndex(GOALIE_COLUMN_NAME)) > 0;
		
		if(goalie){
			return new Goalie(id, name, purchased, shootSpeed, runSpeed,
				tackleSkill, tacklePrevention, savingSkill, teamId);
		} else{
			return new Player(id, name, purchased, shootSpeed, runSpeed,
				tackleSkill, tacklePrevention, savingSkill, teamId);
		}
	}
}
