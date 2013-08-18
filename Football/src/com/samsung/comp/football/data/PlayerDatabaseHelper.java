package com.samsung.comp.football.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class creates and updates the players database and is heavily inspired
 * by: http://www.vogella.com/articles/AndroidSQLite/article.html
 */
public class PlayerDatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "database.db";
	public static final int DATABASE_VERSION = 2;

	public PlayerDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		ProfilesTableManager.onCreate(database);
		TeamsTableManager.onCreate(database);
		PlayersTableManager.onCreate(database);

		Log.w(PlayerDatabaseHelper.class.getName(), "Creating DB... "
				+ "version " + DATABASE_VERSION);
		Log.v("GameDB", "creating...");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// As there hasn't yet been any updates to the database, this is
		// redundant

		ProfilesTableManager.onUpgrade(db, oldVersion, newVersion);
		TeamsTableManager.onUpgrade(db, oldVersion, newVersion);
		PlayersTableManager.onUpgrade(db, oldVersion, newVersion);

		Log.w(PlayerDatabaseHelper.class.getName(), "Upgrading DB... "
				+ oldVersion + " to " + newVersion);
		Log.v("GameDB", "upgrading...");
	}

}
