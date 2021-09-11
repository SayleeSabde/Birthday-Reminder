package com.example.bdayreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {
    EditText name_et,msg_et,phone_et,date_et,time_et;
    String id_string,name_string,msg_string,phone_string,date_string,time_string;
    Button update_button, delete_button;
    private int InstanceId;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        name_et = findViewById(R.id.name_update);
        msg_et = findViewById(R.id.Message_update);
        phone_et = findViewById(R.id.Phone_update);
        date_et = findViewById(R.id.Date_update);
        time_et = findViewById(R.id.Time_update);

        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setTitle(name_string);
        }
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper myDB = new DatabaseHelper(UpdateActivity.this);
                name_string = name_et.getText().toString().trim();
                msg_string = msg_et.getText().toString().trim();
                phone_string = phone_et.getText().toString().trim();
                date_string = date_et.getText().toString().trim();
                time_string = time_et.getText().toString().trim();
                myDB.UpdateData(id_string,name_string,msg_string,phone_string,date_string,time_string);
                cancelAlarm(InstanceId);
                setAlarm(date_string,time_string,InstanceId,phone_string,msg_string,name_string);
            }
        });
        date_et.setInputType(InputType.TYPE_NULL);
        time_et.setInputType(InputType.TYPE_NULL);
        date_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(date_et);
            }
        });
        time_et.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                showTimeDialog(time_et);
            }
        });

    }

    private void setAlarm(String date_string, String time_string, int InstanceID,String phone,String msg, String name) {
        int year = Integer.parseInt(date_string.substring(0,4));
        int month = Integer.parseInt(date_string.substring(5,7));
        int day = Integer.parseInt(date_string.substring(8,10));
        int hour = Integer.parseInt(time_string.substring(0,2));
        int minute = Integer.parseInt(time_string.substring(3,5));

        Calendar calendar  = Calendar.getInstance();
        calendar.set(year,month,day,hour,minute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        //setAlarm(calendar.getTimeInMillis(),msg.getText().toString().trim(),phone.getText().toString().trim());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(UpdateActivity.this,AlarmReceiver.class);
                intent.putExtra("Name",name.toString().trim());
                intent.putExtra("Phone",phone.toString().trim());
                intent.putExtra("Message",msg.toString().trim());
                pendingIntent = PendingIntent.getBroadcast(this,InstanceID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
                Toast.makeText(UpdateActivity.this, "Alarm set successfully ", Toast.LENGTH_SHORT).show();
            }
            else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                Toast.makeText(UpdateActivity.this, "SMS couldn't schedule", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void cancelAlarm(int instanceId) {

        Intent intent = new Intent(this,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,instanceId,intent,0);
        if(alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this,"Alarm Cancelled",Toast.LENGTH_LONG).show();
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete "+name_string+"'s birthday reminder?");
        builder.setMessage("Are you sure you want to delete "+name_string+"'s birthday reminder?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseHelper myDB = new DatabaseHelper(UpdateActivity.this);
                cancelAlarm(InstanceId);
                myDB.deleteOneRow(id_string);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        builder.create().show();
    }
    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("name")
           && getIntent().hasExtra("msg") && getIntent().hasExtra("phone")
           && getIntent().hasExtra("date") && getIntent().hasExtra("time")){
            //Getting data from intent
            id_string = getIntent().getStringExtra("id");
            name_string = getIntent().getStringExtra("name");
            msg_string = getIntent().getStringExtra("msg");
            phone_string = getIntent().getStringExtra("phone");
            date_string = getIntent().getStringExtra("date");
            time_string = getIntent().getStringExtra("time");

            //setting intent data
            name_et.setText(name_string);
            msg_et.setText(msg_string);
            phone_et.setText(phone_string);
            date_et.setText(date_string);
            time_et.setText(time_string);

            InstanceId = Integer.parseInt(id_string);

        }
        else{
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }
    private void showTimeDialog(EditText time){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfday, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfday);
                calendar.set(Calendar.MINUTE,minute);
                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");
                time.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new TimePickerDialog(UpdateActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }
    private void showDateDialog(EditText date){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                date.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new DatePickerDialog(UpdateActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}