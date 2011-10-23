package com.bilal.schedulemytweets;

public class Tweet {
	private String tweet_text;
	private long tweet_time;
	private Integer tweet_id;
	
	Tweet(String _tweet_text, long _tweet_time, Integer _tweet_id) {
		tweet_text = _tweet_text;
		tweet_time = _tweet_time;
		tweet_id = _tweet_id;
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
}
