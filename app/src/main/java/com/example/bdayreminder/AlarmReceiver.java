package com.example.bdayreminder;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, DestinationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
        String name = intent.getStringExtra("Name");
        String msg = intent.getStringExtra("Message");
        String Phone = intent.getStringExtra("Phone");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"foxandroid")
                .setSmallIcon(R.drawable.bday_reminder_app_logo)
                .setContentTitle(name+"'s Birthday Reminder")
                .setContentText("Wish "+name+" a very happy birthday!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Phone, null, msg, null, null);
    }
}