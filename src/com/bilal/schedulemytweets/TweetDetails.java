package com.bilal.schedulemytweets;

import java.util.*;

import com.google.ads.*;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.database.sqlite.*;
import android.content.*;

public class TweetDetails extends Activity {
	private SQLiteTweetDB tweet_db_helper;
	
	private Tweet tweet;
	
	private AdView adview;
	
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
		
		adview = new AdView(this,AdSize.BANNER,getString(R.string.ad_publisher_id));
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout_tweet_details);
        LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_params.weight = 0;
        adview.setLayoutParams(layout_params);
        layout.addView(adview);
        
        AdRequest request = new AdRequest();
        request.addTestDevice(AdRequest.TEST_EMULATOR);
        adview.loadAd(request);
	}
	
    @Override
    public void onDestroy() {
        adview.destroy();
        super.onDestroy();
    }
	
	public void onTweetDeleteClick(View v) {
		final SQLiteDatabase tweet_db = tweet_db_helper.getWritableDatabase();
		final String query = "DELETE FROM tweets WHERE id = " + tweet.get_tweet_id();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.delete_tweet_dialog_message));
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				tweet_db.execSQL(query); 
				
				tweet_db.close();
				
				Intent goBackToListTweets = new Intent(TweetDetails.this, ListTweetsActivity.class);
				finish();
				startActivity(goBackToListTweets);
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				tweet_db.close();
			}
		});
		AlertDialog delete_dialog = builder.create();
		delete_dialog.show();
		
	}
}
