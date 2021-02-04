package com.example.myapplication.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.db.DBHelper;
import com.example.myapplication.mqttHelper.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Alarm  extends BroadcastReceiver {

    MqttHelper mqttHelper;
    DBHelper dbHelperr;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mylockingAoo");
        wl.acquire();
        dbHelperr = new DBHelper(context);

        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example

        Intent i = new Intent();
        i.setClassName("com.example.myapplication", "com.example.myapplication.MainActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);


























        //MainActivity.deleteCache(context);

        wl.release();
    }

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60, pi);
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


    private void mqttSet(final Context context){

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms



        mqttHelper = new MqttHelper(context);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                addTelNumberToDb(new String(message.getPayload()));
                Log.d("mqttMess",new String(message.getPayload()) );
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

            }
        }, 4000);
    }

    private void addTelNumberToDb(String s) {


        if (!dbHelperr.getAll().contains(s)){
            dbHelperr.addOne(s);
        }
}}
