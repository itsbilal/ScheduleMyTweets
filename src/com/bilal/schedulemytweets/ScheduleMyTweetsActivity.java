package com.bilal.schedulemytweets;

import java.util.*;
import java.util.regex.Pattern;

import oauth.signpost.*;
import oauth.signpost.commonshttp.*;
import oauth.signpost.basic.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.impl.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import org.apache.http.protocol.HTTP;

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
	
    private CommonsHttpOAuthConsumer OAuthConsumer;
    private OAuthProvider OAuthProvider;
	
	private static final String consumer_key="ab1vkYWUfwk26ARsP0ckA";
	private static final String consumer_secret="Ze6M5OdHMkdi1qfyGfk5MCy5t0NvcQzWTjnabTcTsHI";

	private static final String CALLBACKURL = "app://schedulemytweets";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        
        if (sp.contains("access_token") != true) {
        	setup_twitter_auth();
        } else {
            Log.d(TAG,"Access token: " + sp.getString("access_token", "heynow") + "\nAccess Token Secret: " + sp.getString("access_token_secret", "beep"));
            OAuthConsumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
            OAuthConsumer.setTokenWithSecret(sp.getString("access_token", "beep"), sp.getString("access_token_secret", "beep"));
        }
        
        Spinner dropdown_duration = (Spinner) findViewById(R.id.dropdown_duration);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.dropdown_duration_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown_duration.setAdapter(adapter);
        dropdown_duration.setOnItemSelectedListener(this);
        
        fill_time_options();
    }
    
    private void setup_twitter_auth() {
    	
    	try {
    		
    	    OAuthConsumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
    	    OAuthProvider = new DefaultOAuthProvider("http://twitter.com/oauth/request_token",
    	                                            "http://twitter.com/oauth/access_token",
    	                                            "http://twitter.com/oauth/authorize");
    	    
    	    String authUrl = OAuthProvider.retrieveRequestToken(OAuthConsumer, CALLBACKURL);
    	    
    	    // Open the browser
    	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        SharedPreferences settings = getPreferences(MODE_PRIVATE);

        Uri uri = intent.getData();
        
        if (settings.contains("access_token") == true) { // Already added key
        	return;
        }

        //Check if you got NewIntent event due to Twitter Call back only

        if (uri != null && uri.toString().startsWith(CALLBACKURL)) {

            String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

            try {
                // this will populate token and token_secret in consumer

                OAuthProvider.retrieveAccessToken(OAuthConsumer, verifier);
                String userKey = OAuthConsumer.getToken();
                String userSecret = OAuthConsumer.getTokenSecret();

                // Save user_key and user_secret in user preferences and return

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("access_token", userKey);
                editor.putString("access_token_secret", userSecret);
                editor.commit();
                
                Log.d(TAG,"Access token: " + userKey + "\nAccess Token Secret: " + userSecret);

            } catch(Exception e){
            	e.printStackTrace();
            }
        }

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
    	try{
	    	HttpClient http_client = new DefaultHttpClient();
	    	HttpPost post_tweet = new HttpPost("http://api.twitter.com/1/statuses/update.json");
	    	
	    	List<NameValuePair> post_params = new ArrayList<NameValuePair>();
	    	post_params.add(new BasicNameValuePair("status", tweet));
	    	
	    	post_tweet.setEntity(new UrlEncodedFormEntity(post_params, HTTP.UTF_8));
	    	
	    	OAuthConsumer.sign(post_tweet);
	    	http_client.execute(post_tweet);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
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