package com.samsung.comp.football.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.comp.football.Profile;

/**
 * A helper class for querying and other iterations with the SQLite Profiles
 * table.
 */
public class ProfilesTableManager {

	protected static final String PROFILES_TABLE_NAME = "profiles";

	protected static final String PROFILE_ID_COLUMN_NAME = "_id";
	protected static final String FUNDS_COLUMN_NAME = "funds";

	private static final String CREATE_PROFILES_TABLE = "create table "
			+ PROFILES_TABLE_NAME + "(" + PROFILE_ID_COLUMN_NAME
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + FUNDS_COLUMN_NAME
			+ " integer not null" + ");";

	protected ProfilesTableManager(SQLiteDatabase database) {
	}

	protected static void onCreate(SQLiteDatabase database) {
		Log.v("GameDB", "Creating Profiles Table");
		database.execSQL(CREATE_PROFILES_TABLE);
		addDefaultProfile(database);
	}

	protected static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(ProfilesTableManager.class.getName(),
				"Upgrading profiles table from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + PROFILES_TABLE_NAME);
		onCreate(db);
	}

	protected static void dropAndRecreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + PROFILES_TABLE_NAME);
		onCreate(db);
	}

	private static void addDefaultProfile(SQLiteDatabase database) {

		Profile profile = new Profile(0, 0);

		insertProfile(database, profile);
		insertProfile(database, profile);
	}

	private static void insertProfile(SQLiteDatabase database, Profile profile) {
		Log.v(PlayerDataSource.class.toString(),
				"adding profile: " + profile.toString());

		ContentValues values = new ContentValues();
		values.put(FUNDS_COLUMN_NAME, profile.getFunds());

		Log.v("db", "profile_id: " + profile.getProfileID());
		Log.v("db", "profile_funds: " + profile.getFunds());
		database.insert(PROFILES_TABLE_NAME, null, values);
	}

	private static Profile cursorToProfile(Cursor cursor) {

		int profileID = cursor.getInt(cursor
				.getColumnIndex(PROFILE_ID_COLUMN_NAME));

		int funds = cursor.getInt(cursor.getColumnIndex(FUNDS_COLUMN_NAME));

		return new Profile(profileID, funds);
	}

	public static Profile getProfile(SQLiteDatabase database, int profileID) {
		Cursor cursor = database.query(PROFILES_TABLE_NAME, null,
				PROFILE_ID_COLUMN_NAME + "=? ",
				new String[] { Integer.toString(profileID) }, null, null, null);
		cursor.moveToFirst();
		return cursorToProfile(cursor);
	}

}

