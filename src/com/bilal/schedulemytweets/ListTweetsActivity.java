package com.bilal.schedulemytweets;

import java.util.*;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class ListTweetsActivity extends ListActivity {
	
	private String TAG="ScheduleMyTweets";
	
	private Twitter twitter_instance;
	
	private ListAdapter tweets_list_adapter;
	private List<Tweet> tweets_list;
	
	SQLiteTweetDB tweet_db_helper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list_tweets); // Necessary for titlebar to come up properly
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        
        tweets_list = new ArrayList<Tweet>();
        //tweets_list.add(new Tweet("Hi",1231242,1)); // TODO: This is just a test
        tweets_list_adapter = new TweetListAdapter(this,tweets_list);
        setListAdapter(tweets_list_adapter);
        
        /*TextView empty_text_view = new TextView(this);
        empty_text_view.setText(getString(R.string.empty_tweet_list_text));
        getListView().setEmptyView(empty_text_view);*/
        
        tweet_db_helper = new SQLiteTweetDB(this);
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        
        if (sp.contains("access_token") != true) {
        	
        	twitter_instance = new Twitter(null, null);
        	
        	// Open OAuth login page
        	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_instance.setup_twitter_auth())));
        } else {
            Log.d(TAG,"Access token: " + sp.getString("access_token", "heynow") + "\nAccess Token Secret: " + sp.getString("access_token_secret", "beep"));
            
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
