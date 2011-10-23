package com.bilal.schedulemytweets;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;

public class ListTweetsActivity extends Activity {
	
	private String TAG="ScheduleMyTweets";
	
	private Twitter twitter_instance;
	
	SQLiteTweetDB tweet_db_helper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list_tweets);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        
        tweet_db_helper = new SQLiteTweetDB(this);
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        
        if (sp.contains("access_token") != true) {
        	//setup_twitter_auth();
        	twitter_instance = new Twitter(null, null);
        	
        	// Open OAuth login page
        	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_instance.setup_twitter_auth())));
        } else {
            Log.d(TAG,"Access token: " + sp.getString("access_token", "heynow") + "\nAccess Token Secret: " + sp.getString("access_token_secret", "beep"));
            //OAuthConsumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
            //OAuthConsumer.setTokenWithSecret(sp.getString("access_token", "beep"), sp.getString("access_token_secret", "beep"));
            twitter_instance = new Twitter (sp.getString("access_token", "beep"), sp.getString("access_token_secret", "beep"));
            
            // Start the service
            Intent serviceIntent = new Intent(this, ScheduleMyTweetsService.class);
            serviceIntent.putExtra("twitter_instance", twitter_instance);
            startService(serviceIntent);
        }
	}
        
	@Override
    protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
            
        SharedPreferences settings = getPreferences(MODE_PRIVATE);

        Uri uri = intent.getData();
            
        twitter_instance.store_access_token(settings, uri);
        if (uri != null && uri.toString().startsWith("app://schedulemytweets")) {
    	    Intent serviceIntent = new Intent(this, ScheduleMyTweetsService.class);
    	    serviceIntent.putExtra("twitter_instance", twitter_instance);
    	    startService(serviceIntent);
        }
    }
	
	public void onNewTweetClick(View v) {
		Intent start_new_tweet_activity = new Intent(this, ScheduleMyTweetsActivity.class);
		startActivity(start_new_tweet_activity);
	}
}
