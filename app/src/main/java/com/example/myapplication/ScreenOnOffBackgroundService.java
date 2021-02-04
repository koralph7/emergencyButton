package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.ScreenOnOffReceiver;
import com.example.myapplication.alarm.Alarm;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class ScreenOnOffBackgroundService extends Service {

    private ScreenOnOffReceiver screenOnOffReceiver = null;
    Alarm alarm = new Alarm();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();




        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
        startForeground(1, new Notification());


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        listenToOnOff();
        alarm.setAlarm(this);

        return Service.START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        alarm.setAlarm(this);
    }

    public void listenToOnOff(){

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");


        intentFilter.setPriority(100);


        screenOnOffReceiver = new ScreenOnOffReceiver();


        registerReceiver(screenOnOffReceiver, intentFilter);

        Log.d("bla", "Service onCreate: screenOnOffReceiver is registered.");

    }





    @Override
    public void onDestroy() {



        super.onDestroy();


        if(screenOnOffReceiver!=null)
        {
            unregisterReceiver(screenOnOffReceiver);
            Log.d("bla", "Service onDestroy: screenOnOffReceiver is unregistered.");
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, CallBroadcastReceiver.class);
        this.sendBroadcast(broadcastIntent);

    }
}
