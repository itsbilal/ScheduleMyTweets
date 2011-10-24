package com.bilal.schedulemytweets;

import android.view.*;
import android.widget.*;
import android.content.*;

import java.util.*;

public class TweetListAdapter extends BaseAdapter {
	
	private List<Tweet> tweets_list = new ArrayList<Tweet>();
	
	private Context context;
	
	TweetListAdapter(Context _context, List<Tweet> _tweets_list) {
		context = _context;
		
		tweets_list = _tweets_list;
	}

	public int getCount() {
		return tweets_list.size();
	}

	public Object getItem(int arg0) {
		return tweets_list.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		Tweet tweet = (Tweet) tweets_list.get(arg0);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
		
		RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.list_tweet_item, null, false);
		
		TextView tweet_text_view = (TextView)layout.findViewById(R.id.list_tweet_tweet);
		tweet_text_view.setText(tweet.get_tweet_text());
		
		TextView tweet_time_view = (TextView) layout.findViewById(R.id.list_tweet_time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(tweet.get_tweet_time() * 1000);
		Date date = calendar.getTime();
		tweet_time_view.setText(date.toString());
		
		return layout;
	}

}
