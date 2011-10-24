package com.bilal.schedulemytweets;

import java.util.*;

import android.app.*;
import android.os.*;
import android.widget.*;

public class TweetDetails extends Activity {
	private SQLiteTweetDB tweet_db_helper;
	
	private Tweet tweet;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_details);
		
		tweet_db_helper = new SQLiteTweetDB(this);
		
		tweet = (Tweet)getIntent().getParcelableExtra("tweet");
		
		TextView tweet_text_view = (TextView) findViewById(R.id.tweet_details_text_view);
		TextView time_text_view = (TextView) findViewById(R.id.tweet_details_time_view);
		
		tweet_text_view.setText(tweet.get_tweet_text());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(((long)tweet.get_tweet_time() * 1000));
		Date date = calendar.getTime();
		
		time_text_view.setText(date.toString());
	}
}
