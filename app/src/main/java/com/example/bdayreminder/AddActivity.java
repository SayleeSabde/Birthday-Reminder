package com.example.bdayreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    DatePickerDialog.OnDateSetListener setListener;
    EditText name, msg, date, time, phone;
    Button add_button, schedule_text;


    Calendar c;
    public int year,month,day,hour,Minute;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int IntentId;
    private int InstanceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        add_button = findViewById(R.id.add_button);
        name = findViewById(R.id.name);
        msg =  findViewById(R.id.Message);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.Time);
        phone = findViewById(R.id.Phone);
        schedule_text = findViewById(R.id.schedule_text);
        schedule_text.setOnClickListener(view -> {
            Calendar calendar  = Calendar.getInstance();
            calendar.set(year,month,day,hour,Minute);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            //setAlarm(calendar.getTimeInMillis(),msg.getText().toString().trim(),phone.getText().toString().trim());
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(AddActivity.this,AlarmReceiver.class);
                    intent.putExtra("Name",name.getText().toString().trim());
                    intent.putExtra("Phone",phone.getText().toString().trim());
                    intent.putExtra("Message",msg.getText().toString().trim());
                    pendingIntent = PendingIntent.getBroadcast(this,InstanceID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
                    Toast.makeText(AddActivity.this, "Alarm set successfully ", Toast.LENGTH_SHORT).show();
                }
                else {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                    Toast.makeText(AddActivity.this, "SMS couldn't schedule", Toast.LENGTH_SHORT).show();
                }
            }

        });
        add_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper myDB = new DatabaseHelper(AddActivity.this);
                Cursor cursor = myDB.addEvent(name.getText().toString().trim(),
                              msg.getText().toString().trim(),
                              phone.getText().toString().trim(),
                              date.getText().toString().trim(),
                              time.getText().toString().trim());
                if(cursor.getCount() == 0){
                    Toast.makeText(AddActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                }
                else{
                    while(cursor.moveToNext()){
                        InstanceID = Integer.parseInt(cursor.getString(0));
                    }
                }
            }
        });
        date.setInputType(InputType.TYPE_NULL);
        time.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(date);
            }
        });
        time.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View view) {
                showTimeDialog(time);
            }
        });
    }
    private void showTimeDialog(EditText time){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfday, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfday);
                calendar.set(Calendar.MINUTE,minute);
                hour = hourOfday;
                Minute = minute;
                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");
                time.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };
        new TimePickerDialog(AddActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }
    private void showDateDialog(EditText date){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1-1;
                day = i2;
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                date.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };
        new DatePickerDialog(AddActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void setAlarm(long timeInMillis, String msg, String Phone){
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this, AlarmReceiver.class);
        intent.putExtra("Phone",Phone);
        intent.putExtra("msg",msg);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
        am.set(AlarmManager.RTC_WAKEUP,timeInMillis,pendingIntent);

        Toast.makeText(this, "Message is scheduled!", Toast.LENGTH_SHORT).show();

    }
}