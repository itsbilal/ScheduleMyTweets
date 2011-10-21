package com.bilal.schedulemytweets;

import java.util.*;
import java.util.regex.Pattern;

import android.app.Activity;
import android.util.*;
import android.view.*;
import android.os.Bundle;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.*;
import android.net.*;

public class ScheduleMyTweetsActivity extends Activity implements OnItemSelectedListener {
	private Hashtable<String,Integer> time_options = new Hashtable<String,Integer>();
	
	private String TAG="ScheduleMyTweets";
	
	private Twitter twitter_instance;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
        }
        
        Spinner dropdown_duration = (Spinner) findViewById(R.id.dropdown_duration);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.dropdown_duration_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown_duration.setAdapter(adapter);
        dropdown_duration.setOnItemSelectedListener(this);
        
        
        
        fill_time_options();
    }
    
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        SharedPreferences settings = getPreferences(MODE_PRIVATE);

        Uri uri = intent.getData();
        
        twitter_instance.store_access_token(settings, uri);
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
    	
    	// TODO: This is just a placeholder to test tweeting
    	twitter_instance.tweet(tweet);
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