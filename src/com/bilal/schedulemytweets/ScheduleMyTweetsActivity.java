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
	private Integer mHour;
	private Integer mMinute;
	
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
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = null;
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
    	try{
    	
    	String tweet = ((EditText)findViewById(R.id.edittext_tweet)).getText().toString();
    	
    	SQLiteDatabase tweet_db = tweet_db_helper.getWritableDatabase();
    	
    	long selected_time = 0;
    	
    	if (((RadioButton)findViewById(R.id.radiobutton_time)).isChecked() == true) {
    		
    		if (mMinute == null) {  // Time is not set
    			tweet_db.close();
    			
    			Toast.makeText(this, getString(R.string.toast_time_unset), Toast.LENGTH_SHORT).show();
    			return;
    		}
    		
    		Calendar calendar = Calendar.getInstance();
    		calendar.set(mYear, mMonth, mDay);
    		calendar.set(Calendar.HOUR_OF_DAY, mHour);
    		calendar.set(Calendar.MINUTE, mMinute);
    		selected_time = (calendar.getTimeInMillis() / 1000);
    		
    	} else {
	    	Spinner dropdown_duration = (Spinner) findViewById(R.id.dropdown_duration);
	    	
	    	selected_time = time_options.get(dropdown_duration.getSelectedItem());
	    	selected_time = selected_time + ((long)System.currentTimeMillis() / 1000);
    	}
    	
    	String sql_exec = "INSERT INTO tweets (tweet, time) VALUES ('" +
				 tweet + "', " + selected_time + ");";
    	Log.d(TAG, sql_exec);
    	tweet_db.execSQL(sql_exec);
    	
    	tweet_db.close();
    	
    	Intent backToListTweets = new Intent(this, ListTweetsActivity.class);
    	finish();
    	startActivity(backToListTweets);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
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
    	
    	TimePickerDialog.OnTimeSetListener mOnTimeListener
    					= new TimePickerDialog.OnTimeSetListener() {
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								mHour = hourOfDay;
								mMinute = minute;
								
								String button_label_string = Integer.toString(mHour);
								button_label_string += ":" + mMinute.toString(); 
								((Button)findViewById(R.id.button_select_time)).setText(button_label_string);
							}
						};
		
		Calendar calendar = Calendar.getInstance();
		Integer current_minute = calendar.get(Calendar.MINUTE);
		TimePickerDialog tpd = new TimePickerDialog(this, mOnTimeListener, mHour, current_minute, false);
		tpd.show();
    }
    
    public void onItemSelected(AdapterView<?> parent,
        View view, int pos, long id) {
          String selected_item = parent.getItemAtPosition(pos).toString();
          Log.d(TAG,time_options.get(selected_item).toString());
          
          ((RadioButton)findViewById(R.id.radiobutton_duration)).setChecked(true);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
}