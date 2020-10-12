package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    private final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";
//    private Context mContext;
//    Vibrator v = (Vibrator) mContext.getSystemService();

    Activity activity;

    private Context context;

    MainActivity mainActivity = new MainActivity();
    List<Long> ifSwitched = new ArrayList<>();

    public ScreenOnOffReceiver(){}

    public ScreenOnOffReceiver(Context context){
        this.context=context;
    }
//    MainActivity mainActivity = new MainActivity();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();



        if(Intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.d(SCREEN_TOGGLE_TAG, "pierwszy if");
            addToList();
        }
        else if(Intent.ACTION_SCREEN_ON.equals(action))
        {
            addToList();
            Log.d(SCREEN_TOGGLE_TAG, "drugi if");
        }






//        else if(Intent.ACTION_SCREEN_ON.equals(action))
//        {
//
//            Log.d(SCREEN_TOGGLE_TAG, action);
//        }
//
//        else if(Intent.ACTION_MEDIA_BUTTON.equals(action)) {
//
//            Log.d(SCREEN_TOGGLE_TAG, action);
//
//
//        }
    }


    public void sendSMSMessage() {
//        phoneNo = txtphoneNo.getText().toString();
//        message = txtMessage.getText().toString();

        //Toast.makeText(this, "balbala", Toast.LENGTH_LONG).show();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("513849113", null, "Button Panic's working, after clicking 5x times", null, null);

    }

//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage("668880445", null, "blablabla", null, null);
//                    Toast.makeText(getApplicationContext(), "SMS sent.",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//        }
//
//    }

    public void addToList(){



        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("Time in milliseconds using Date class: " + timeMilli);

        System.out.println("rozmiar listy to" +ifSwitched.size());
        ifSwitched.add(timeMilli);

        String timeMillis = String.valueOf(timeMilli);

        Log.d("aaa", timeMillis);

        if (ifSwitched.size() > 4) {
            long diff = ifSwitched.get(4) - ifSwitched.get(0);
            long secdiff = diff/1000;
            String timeSec = String.valueOf(secdiff);

            Log.d("aaa", timeSec);

            if (secdiff < 30) {


                sendSMSMessage();

                //mainActivity.sendSMSMessage();
                ifSwitched.clear();
            }

            else  ifSwitched.clear();

        }

    }

}