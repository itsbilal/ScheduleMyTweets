package com.bilal.schedulemytweets;

import android.app.*;
import android.os.*;
import android.content.*;
import android.util.*;
import android.database.sqlite.*;

import java.util.*;

public class ScheduleMyTweetsService extends Service {
	
	private Timer timer = new Timer();
	//private Context me;
	
	private static final String TAG="ScheduleMyTweets";
	
	private SQLiteTweetDB tweet_db_helper;
	private SQLiteDatabase tweet_db;
	
	private class timer_task extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d(TAG,"Hi! Its working!");
			
			
			
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
		
		return START_STICKY;
	}

}
