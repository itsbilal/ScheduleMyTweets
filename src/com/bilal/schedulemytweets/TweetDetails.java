package com.bilal.schedulemytweets;

import java.util.*;

import android.app.*;
import android.os.*;
import android.view.Window;
import android.widget.*;

public class TweetDetails extends Activity {
	private SQLiteTweetDB tweet_db_helper;
	
	private Tweet tweet;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.tweet_details);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title3);

		
		tweet_db_helper = new SQLiteTweetDB(this);
		
		tweet = (Tweet)getIntent().getParcelableExtra("tweet");
		
		TextView tweet_text_view = (TextView) findViewById(R.id.tweet_details_text_view);
		TextView time_text_view = (TextView) findViewById(R.id.tweet_details_time_view);
		
		String tweet_string = "\'";
		tweet_string += tweet.get_tweet_text();
		tweet_string += "\""; // Hack to fix the quote issue
		tweet_text_view.setText(tweet_string);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(((long)tweet.get_tweet_time() * 1000));
		Date date = calendar.getTime();
		
		time_text_view.setText(date.toString());
	}
}
