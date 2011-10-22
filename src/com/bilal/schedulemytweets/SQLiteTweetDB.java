package com.bilal.schedulemytweets;

import android.content.*;
import android.database.sqlite.*;

public class SQLiteTweetDB extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
    //private static final String DICTIONARY_TABLE_NAME = "dictionary";
    //private static final String DICTIONARY_TABLE_CREATE =
      //          "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
        //        KEY_WORD + " TEXT, " +
          //      KEY_DEFINITION + " TEXT);";
	private static final String TWEET_TABLE_CREATE = "CREATE TABLE tweets (" +
								"id INTEGER PRIMARY KEY," +
								"tweet TEXT, time BIGINT" +
								");";
	
	SQLiteTweetDB (Context context) {
		super(context,"schedulemytweets_tweets", null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TWEET_TABLE_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		// Do nothing
	}
	
}
