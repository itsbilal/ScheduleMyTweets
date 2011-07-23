package com.bilal.schedulemytweets;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ScheduleMyTweetsService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}

}
