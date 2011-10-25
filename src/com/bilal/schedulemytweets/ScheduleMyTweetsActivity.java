package com.bilal.schedulemytweets;

import java.util.*;
import java.util.regex.Pattern;

import android.app.Activity;
import android.util.*;
import android.view.*;
import android.os.Bundle;
import android.widget.*;
import android.content.*;
import android.database.sqlite.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class ScheduleMyTweetsActivity extends Activity implements OnItemSelectedListener {
	private Hashtable<String,Integer> time_options = new Hashtable<String,Integer>();
	
	private String TAG="ScheduleMyTweets";
	
	SQLiteTweetDB tweet_db_helper;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title2);
        
        tweet_db_helper = new SQLiteTweetDB(this);
        
        Spinner dropdown_duration = (Spinner) findViewById(R.id.dropdown_duration);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.dropdown_duration_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown_duration.setAdapter(adapter);
        dropdown_duration.setOnItemSelectedListener(this);
        
        fill_time_options();
    }
    
    
    private void fill_time_options() {
    	String[] options = getResources().getStringArray(R.array.dropdown_duration_options);
    	for(String option : options){
    		String stripped_option = option.substring(6); // Strip the After prefix
    		if (option.indexOf("minutes") != -1) { // option is in minutes
    			String no_of_minutes = stripped_option.split(Pattern.quote(" "))[0];
    			time_options.put(option, (Integer.parseInt(no_of_minutes) * 60));
    		} else {
    			String no_of_hours = stripped_option.split(Pattern.quote(" "))[0];
    			time_options.put(option, (Integer.parseInt(no_of_hours) * 3600));
    		}
    	}
    }
    
    public void onButtonSubmit(View v) {
    	
    	String tweet = ((EditText)findViewById(R.id.edittext_tweet)).getText().toString();
    	
    	Spinner dropdown_duration = (Spinner) findViewById(R.id.dropdown_duration);
    	
    	long selected_time_after = time_options.get(dropdown_duration.getSelectedItem());
    	
    	SQLiteDatabase tweet_db = tweet_db_helper.getWritableDatabase();
    	
    	tweet_db.execSQL("INSERT INTO tweets (tweet, time) VALUES ('" +
    					 tweet + "', " + (((long)System.currentTimeMillis() / 1000) +
    							          selected_time_after) + ");");
    	tweet_db.close();
    	
    	Intent backToListTweets = new Intent(this, ListTweetsActivity.class);
    	finish();
    	startActivity(backToListTweets);
    }
    
    public void onItemSelected(AdapterView<?> parent,
        View view, int pos, long id) {
          String selected_item = parent.getItemAtPosition(pos).toString();
          Log.d(TAG,time_options.get(selected_item).toString());
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
}