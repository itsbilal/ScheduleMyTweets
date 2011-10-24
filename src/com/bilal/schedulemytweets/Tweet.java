package com.bilal.schedulemytweets;

import android.os.*;

public class Tweet implements Parcelable {
	private String tweet_text;
	private long tweet_time;
	private Integer tweet_id;
	
	public Tweet(String _tweet_text, long _tweet_time, Integer _tweet_id) {
		tweet_text = _tweet_text;
		tweet_time = _tweet_time;
		tweet_id = _tweet_id;
	}
	
	private Tweet(Parcel parcel) {
		tweet_text = parcel.readString();
		tweet_time = parcel.readLong();
		tweet_id = parcel.readInt();
	}
	
	public String get_tweet_text() {
		return tweet_text;
	}
	
	public long get_tweet_time() {
		return tweet_time;
	}
	
	public Integer get_tweet_id() {
		return tweet_id;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(tweet_text);
		arg0.writeLong(tweet_time);
		arg0.writeInt(tweet_id);
	}
	
	public static final Parcelable.Creator<Tweet> CREATOR
			= new Parcelable.Creator<Tweet>() {

				public Tweet createFromParcel(Parcel source) {
					return new Tweet(source);
				}

				public Tweet[] newArray(int size) {
					return new Tweet[size];
				}
			};
}
