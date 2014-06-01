package com.bearcat.remembear.remembear;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Creating a new reminder.
 *
 * TODO: Clean up this activity, update deprecated methods.
 */
public class ReminderActivity extends ActionBarActivity {

    public static final String
        REMINDER_NAME = "Name",
        REMINDER_DESCRIPTION = "Description",
        REMINDER_NOTIFY_COUNT = "NotifyCount";

    private static final int
        DATE_DIALOG_ID = 1,
        TIME_DIALOG_ID = 0;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    private Calendar calendar;
    private EditText nameEdit;
    private EditText descEdit;
    private Context mContext;
    private boolean dateFlag = false;
    private boolean timeFlag = false;
    private String time;
    private String contentTitle;
    private String contentText;

    public static int notificationCount;
    public Button dateButton;
    public Button timeButton;
    public Button reminButton;
    public TextListener textListener;

    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear+1;
                mDay = dayOfMonth;
                dateFlag = true;
            }
        };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                timeFlag = true;
            }
        };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_reminder);

        mContext = this;
        textListener = new TextListener();

        // Initialize buttons
        dateButton = (Button) findViewById(R.id.dateButton);
        timeButton = (Button) findViewById(R.id.timeButton);
        reminButton = (Button) findViewById(R.id.reminderButton);
        dateButton.setEnabled(false);
        timeButton.setEnabled(false);
        reminButton.setEnabled(false);

        // Set listeners
        nameEdit = (EditText) findViewById(R.id.reminderNameInput);
        nameEdit.addTextChangedListener(textListener);
        descEdit = (EditText) findViewById(R.id.reminderDescriptionInput);
        descEdit.addTextChangedListener(textListener);

        // Initialize time
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
    }

    public class TextListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(descEdit.getText().length() == 0 | nameEdit.getText().length() == 0){
                dateButton.setEnabled(false);
                timeButton.setEnabled(false);
                reminButton.setEnabled(false);
            }
            else if(descEdit.getText().length() > 0 & nameEdit.getText().length() > 0){
                dateButton.setEnabled(true);
                timeButton.setEnabled(true);
                reminButton.setEnabled(true);
            }
        }
    }

    /**
     * Sets an alarm for the given date and time,
     * to be picked up by the {@link ReminderAlarm}.
     *
     * @param view
     */
    public void onReminderClicked(View view){
        if (dateFlag & timeFlag == true) {
            notificationCount  = notificationCount + 1;

            dateFlag = false;
            timeFlag = false;
            time = mYear+"-"+mMonth+"-"+mDay+" "+mHour+"-"+mMinute;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm");

            Date date = null;
            try {
                date = dateFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long reminderTime = date.getTime();

            contentTitle = nameEdit.getText().toString();
            contentText = descEdit.getText().toString();

            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent(mContext, ReminderAlarm.class);
            notificationIntent.putExtra(REMINDER_NAME, contentTitle);
            notificationIntent.putExtra(REMINDER_DESCRIPTION, contentText);
            notificationIntent.putExtra(REMINDER_NOTIFY_COUNT, notificationCount);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notificationCount, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);

            Toast.makeText(mContext, "New remembear set", Toast.LENGTH_LONG).show();

            contentTitle = "";
            contentText = "";
            descEdit.setText("");
            nameEdit.setText("");

        } else if(dateFlag == false | timeFlag == false){
            Toast.makeText(mContext, "Please choose Date & Time", Toast.LENGTH_SHORT).show();
        }
    }

    public void onTimeClicked(View view){
        showDialog(TIME_DIALOG_ID);
    }
    public void onDateClicked(View view){
        showDialog(DATE_DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return super.onCreateDialog(id);
    }

}
