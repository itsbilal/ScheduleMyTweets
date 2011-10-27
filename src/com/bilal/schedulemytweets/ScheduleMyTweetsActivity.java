package com.bilal.schedulemytweets;

import java.util.*;
import java.util.regex.Pattern;

import android.app.*;
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
	
	private Integer mYear;
	private Integer mMonth;
	private Integer mDay;
	
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
        
        // Select this one by default
        ((RadioButton)findViewById(R.id.radiobutton_duration)).setChecked(true);
        
        ((Button)findViewById(R.id.button_select_date)).setTag("today");
        
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
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
    	
    	SQLiteDatabase tweet_db = tweet_db_helper.getWritableDatabase();
    	
    	if (((RadioButton)findViewById(R.id.radiobutton_time)).isChecked() == true) {
    		
    	} else {
	    	Spinner dropdown_duration = (Spinner) findViewById(R.id.dropdown_duration);
	    	
	    	long selected_time_after = time_options.get(dropdown_duration.getSelectedItem());
	    	
	    	
	    	
	    	tweet_db.execSQL("INSERT INTO tweets (tweet, time) VALUES ('" +
	    					 tweet + "', " + (((long)System.currentTimeMillis() / 1000) +
	    							          selected_time_after) + ");");
    	}
    	
    	tweet_db.close();
    	
    	Intent backToListTweets = new Intent(this, ListTweetsActivity.class);
    	finish();
    	startActivity(backToListTweets);
    }
    
    public void onSelectDateClick(View v) {
    	((RadioButton)findViewById(R.id.radiobutton_time)).setChecked(true);
    	
    	DatePickerDialog.OnDateSetListener mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker arg0, int arg1, int arg2,
							int arg3) {
						mYear = arg1;
						mMonth = arg2;
						mDay = arg3;
						
						Calendar calendar = Calendar.getInstance();
						calendar.set(mYear, mMonth, mDay);
						
						String button_label = Integer.toString(calendar.get(Calendar.DATE)) + " ";
						button_label += String.format(Locale.US,"%tB",calendar) + ", ";
						button_label += Integer.toString(calendar.get(Calendar.YEAR));
						
						((Button)findViewById(R.id.button_select_date)).setText(button_label);
					}
    	};
    	
    	DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
    	dpd.show();
    }
    
    public void onSelectTimeClick(View v) {
    	((RadioButton)findViewById(R.id.radiobutton_time)).setChecked(true);

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