package com.bilal.schedulemytweets;

import android.app.*;
import android.os.*;
import android.content.*;
import android.util.*;
import android.database.sqlite.*;
import android.database.*;

import java.util.*;

public class ScheduleMyTweetsService extends Service {
	
	private Timer timer = new Timer();
	//private Context me;
	
	private static final String TAG="ScheduleMyTweets";
	
	private SQLiteTweetDB tweet_db_helper;
	private SQLiteDatabase tweet_db;
	
	private Twitter twitter_instance;
	
	private class timer_task extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d(TAG,"Hi! Its working!");
			
			try {
				String sql_query = "SELECT tweet,time FROM tweets WHERE time BETWEEN " +
									(((long)System.currentTimeMillis() / 1000) - ((long)30)) + " AND " +
									(((long)System.currentTimeMillis() / 1000) + ((long)30))+ ";";
				
				Cursor cursor_result = tweet_db.rawQuery(sql_query, null);
				
				cursor_result.moveToFirst();
				
				while (cursor_result.isAfterLast() == false) {
					String tweet_text = cursor_result.getString(0);
					Log.d(TAG,"Cursor result: " + tweet_text);
					twitter_instance.tweet(tweet_text);
					cursor_result.moveToNext();
				}
				
				cursor_result.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
		//me = this;
		
		tweet_db_helper = new SQLiteTweetDB(this);
		tweet_db = tweet_db_helper.getWritableDatabase();
		
		timer.scheduleAtFixedRate(new timer_task(), 0, 60000);
		
		twitter_instance = (Twitter)intent.getParcelableExtra("twitter_instance");
		
		return START_STICKY;
	}

}
