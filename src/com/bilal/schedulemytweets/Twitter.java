package com.bilal.schedulemytweets;

import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

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

import android.net.Uri;
import android.util.Log;
import android.content.*;

public class Twitter {
	
	private CommonsHttpOAuthConsumer OAuthConsumer;
    private OAuthProvider OAuthProvider;
    
	private static final String consumer_key="ab1vkYWUfwk26ARsP0ckA";
	private static final String consumer_secret="Ze6M5OdHMkdi1qfyGfk5MCy5t0NvcQzWTjnabTcTsHI";

	private static final String CALLBACKURL = "app://schedulemytweets";
	
	private static final String TAG="ScheduleMyTweets";
	
	public Twitter(String access_token, String access_token_secret) {
		if (access_token != null) {
    	    OAuthConsumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
    	    OAuthConsumer.setTokenWithSecret(access_token, access_token_secret);
		}
		
	}
	
	public void store_access_token(SharedPreferences settings, Uri uri) {
		if (settings.contains("access_token") == true) { // Already added key
        	return;
        }

        //Check if you got NewIntent event due to Twitter Call back only

        if (uri != null && uri.toString().startsWith(CALLBACKURL)) {

            String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

            try {
                // Retrieve the access tokens

                OAuthProvider.retrieveAccessToken(OAuthConsumer, verifier);
                String userKey = OAuthConsumer.getToken();
                String userSecret = OAuthConsumer.getTokenSecret();

                // Save the access tokens in shared preferences

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
	
    public String setup_twitter_auth() {
    	
    	try {
    		
    	    OAuthConsumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
    	    OAuthProvider = new DefaultOAuthProvider("http://twitter.com/oauth/request_token",
    	                                            "http://twitter.com/oauth/access_token",
    	                                            "http://twitter.com/oauth/authorize");
    	    
    	    String authUrl = OAuthProvider.retrieveRequestToken(OAuthConsumer, CALLBACKURL);
    	    
    	    // Open the browser
    	    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
    	    
    	    return authUrl;
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public void tweet(final String tweet_text) {
    	try{
	    	new Thread(new Runnable(){
	    		public void run(){
	    			try {
		    			HttpClient http_client = new DefaultHttpClient();
		    	    	HttpPost post_tweet = new HttpPost("http://api.twitter.com/1/statuses/update.json");
		    	    	
		    	    	List<NameValuePair> post_params = new ArrayList<NameValuePair>();
		    	    	post_params.add(new BasicNameValuePair("status", tweet_text));
		    	    	
		    	    	
						post_tweet.setEntity(new UrlEncodedFormEntity(post_params, HTTP.UTF_8));
						
						OAuthConsumer.sign(post_tweet);
		    	    	http_client.execute(post_tweet);
					} catch (Exception e) {
						e.printStackTrace();
					}
	    		}
	    	}).start();
	    	
	    	Log.d(TAG,"Tweeted: " + tweet_text);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
