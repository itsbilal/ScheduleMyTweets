package com.bilal.schedulemytweets;

import java.util.*;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.database.*;
import android.database.sqlite.*;

import com.google.ads.*;

public class ListTweetsActivity extends ListActivity {
	
	private String TAG="ScheduleMyTweets";
	
	private Twitter twitter_instance;
	
	private ListAdapter tweets_list_adapter;
	private List<Tweet> tweets_list;
	private AdView adview;
	
	SQLiteTweetDB tweet_db_helper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list_tweets); // Necessary for titlebar to come up properly
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        
        tweets_list = new ArrayList<Tweet>();
        tweets_list_adapter = new TweetListAdapter(this,tweets_list);
        setListAdapter(tweets_list_adapter);
        
        tweet_db_helper = new SQLiteTweetDB(this);
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        
        fill_list_view_with_tweets();
        
        if (sp.contains("access_token") != true) {
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle(getString(R.string.welcome_dialog_title));
        	builder.setMessage(getString(R.string.welcome_dialog_message));
        	builder.setCancelable(false);
        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
					twitter_instance = new Twitter(null, null);
		        	
		        	// Open OAuth login page
		        	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_instance.setup_twitter_auth())));
				}
			});
        	AlertDialog welcome_dialog = builder.create();
        	welcome_dialog.show();
        	
        } else {
            Log.d(TAG,"Access token: " + sp.getString("access_token", "heynow") + "\nAccess Token Secret: " + sp.getString("access_token_secret", "beep"));
            
            twitter_instance = new Twitter (sp.getString("access_token", "beep"), sp.getString("access_token_secret", "beep"));
            
            // Start the service
            Intent serviceIntent = new Intent(this, ScheduleMyTweetsService.class);
            serviceIntent.putExtra("twitter_instance", twitter_instance);
            startService(serviceIntent);
        }
        
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Create a Tweet object out of the view arg1 and
				// send it to TweetDetails activity
				RelativeLayout layout = (RelativeLayout) arg1;
				TextView textview_tweet = (TextView)layout.findViewById(R.id.list_tweet_tweet);
				String tweet_text = (String)textview_tweet.getText();
				long tweet_time = (Long)layout.getTag(R.id.id2);
				Integer tweet_id = (Integer)layout.getTag(R.id.id1);
				Tweet tweet = new Tweet(tweet_text, tweet_time, tweet_id);
				
				Intent startTweetDetails = new Intent(ListTweetsActivity.this, TweetDetails.class);
				startTweetDetails.putExtra("tweet", tweet);
				startActivity(startTweetDetails);
			}
		});
        
        adview = new AdView(this,AdSize.BANNER,getString(R.string.ad_publisher_id));
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_list_tweets);
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
	
	@Override
	public void onResume() {
		super.onResume();
		fill_list_view_with_tweets();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menu_inflater = getMenuInflater();
		menu_inflater.inflate(R.menu.menu_list_tweets, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		 case R.id.menu_logout:
			 AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 builder.setMessage(getString(R.string.logout_dialog_message));
			 builder.setCancelable(true);
			 builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences.Editor sp = getPreferences(MODE_PRIVATE).edit();
					sp.clear();
					sp.commit();
					finish();
				}
			 });
			 builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			 });
			 AlertDialog logout_dialog = builder.create();
			 logout_dialog.show();
			 
			 return true;
		 case R.id.menu_new_tweet:
			 Intent start_new_tweet_activity = new Intent(this, ScheduleMyTweetsActivity.class);
			 startActivity(start_new_tweet_activity);
			 return true;
		 default:
			 return super.onOptionsItemSelected(item);
		}
	}
        
	private void fill_list_view_with_tweets() {
		try{
			tweets_list.clear();
			
			SQLiteDatabase tweets_db = tweet_db_helper.getReadableDatabase();
			
			String query = "SELECT * FROM tweets WHERE time >= " + ((long)System.currentTimeMillis() / 1000) + " ORDER BY time ASC";
			Cursor cursor = tweets_db.rawQuery(query, null);
			if (cursor == null) { // No results
				return;
			}
			cursor.moveToFirst();
			
			while (cursor.isAfterLast() == false) {
				Tweet current_tweet = new Tweet(cursor.getString(1),
												cursor.getLong(2),
												cursor.getInt(0));
				tweets_list.add(current_tweet);
				
				cursor.moveToNext();
			}
			((BaseAdapter) tweets_list_adapter).notifyDataSetChanged();
			cursor.close();
			tweets_db.close();
		} catch (Exception e) {
			e.printStackTrace();
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
    	    
    	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	    builder.setMessage(getString(R.string.login_dialog_message));
    	    builder.setCancelable(true);
    	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
    	    AlertDialog login_dialog = builder.create();
    	    login_dialog.show();
        }
    }
	
	public void onNewTweetClick(View v) {
		Intent start_new_tweet_activity = new Intent(this, ScheduleMyTweetsActivity.class);
		startActivity(start_new_tweet_activity);
	}
}
