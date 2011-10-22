package com.bilal.schedulemytweets;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.*;
import oauth.signpost.commonshttp.*;
import oauth.signpost.basic.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;
import org.apache.http.protocol.HTTP;

import android.net.Uri;
import android.util.Log;
import android.content.*;
import android.os.*;

public class Twitter implements Parcelable {
	
	private CommonsHttpOAuthConsumer OAuthConsumer;
    private OAuthProvider OAuthProvider;
    
	private static final String consumer_key="ab1vkYWUfwk26ARsP0ckA";
	private static final String consumer_secret="Ze6M5OdHMkdi1qfyGfk5MCy5t0NvcQzWTjnabTcTsHI";

	private static final String CALLBACKURL = "app://schedulemytweets";
	
	private String access_token;
	private String access_token_secret;
	
	private static final String TAG="ScheduleMyTweets";
	
	public Twitter(String access_token_s, String access_token_secret_s) {
		if (access_token_s != null) {
    	    OAuthConsumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
    	    OAuthConsumer.setTokenWithSecret(access_token_s, access_token_secret_s);
    	    access_token = new String(access_token_s);
    	    access_token_secret = new String(access_token_secret_s);
		}
		
	}
	
	private Twitter (Parcel parcel) {
		// You can always assume that the access tokens are already supplied
	    access_token = parcel.readString();
	    access_token_secret = parcel.readString();
		OAuthConsumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
	    OAuthConsumer.setTokenWithSecret(access_token, access_token_secret);
	    
	    Log.d(TAG, "Unpacking parcel");
	    Log.d(TAG,"Access token: " + access_token);
	    Log.d(TAG,"Access token secret: " + access_token_secret);
	    Log.d(TAG,"Consumer Key: " + consumer_key);
	    Log.d(TAG,"Consumer Secret: " + consumer_secret);
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
                access_token = OAuthConsumer.getToken();
                access_token_secret = OAuthConsumer.getTokenSecret();

                // Save the access tokens in shared preferences

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("access_token", access_token);
                editor.putString("access_token_secret", access_token_secret);
                editor.commit();
                
                Log.d(TAG,"Access token: " + access_token + "\nAccess Token Secret: " + access_token_secret);

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

	public int describeContents() {
		// TODO Auto-generated method stub
		// We don't give a shit about this
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(access_token);
		dest.writeString(access_token_secret);
		
		Log.d(TAG, "Packing parcel..");
		Log.d(TAG,"Access token: " + access_token);
	    Log.d(TAG,"Access token secret: " + access_token_secret);
	    Log.d(TAG,"Consumer Key: " + consumer_key);
	    Log.d(TAG,"Consumer Secret: " + consumer_secret);
	}
	
    public static final Parcelable.Creator<Twitter> CREATOR
    		= new Parcelable.Creator<Twitter>() {
    				public Twitter createFromParcel(Parcel in) {
					    return new Twitter(in);
					}
					
					public Twitter[] newArray(int size) {
					    return new Twitter[size];
					}
			};
}
