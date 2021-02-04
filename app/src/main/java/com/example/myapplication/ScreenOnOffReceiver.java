package com.example.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.myapplication.db.DBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    private final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";

    MainActivity mainActivity = null;

    Activity activity;

    private Context context;

    DBHelper dbHelper;




    List<Long> ifSwitched = new ArrayList<>();

    public ScreenOnOffReceiver(){}

    public ScreenOnOffReceiver(Context context){
        this.context=context;
    }
    MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {


        mp = MediaPlayer.create(context, R.raw.bbb);

        String action = intent.getAction();

        dbHelper = new DBHelper(context);

        if(Intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.d(SCREEN_TOGGLE_TAG, "pierwszy if");
            addToList();
        }
        else if(Intent.ACTION_SCREEN_ON.equals(action))
        {
            addToList();
            Log.d(SCREEN_TOGGLE_TAG, "drugi if");
        }






    }

    public void setMainActivityHandler(MainActivity main){
        mainActivity = main;
    }

    public void sendSMSMessage() {
//        phoneNo = txtphoneNo.getText().toString();
//        message = txtMessage.getText().toString();

        //Toast.makeText(this, "balbala", Toast.LENGTH_LONG).show();

        if (!dbHelper.getAll().isEmpty()) {
            for (String telNum : dbHelper.getAll()) {


                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(telNum, null, "Button Panic's working, after clicking 5x times", null, null);

            }

            playTune();
        }

//        mainActivity.playTune();
    }

    public void playTune(){
        mp.start();
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
        long timeMilli = date.getTime();
        ifSwitched.add(timeMilli);
        System.out.println("Time in milliseconds using Date class: " + timeMilli);

        System.out.println("rozmiar listy to" +ifSwitched.size());

        if (ifSwitched.size()==2) {
            //String timeMillis = String.valueOf(timeMilli);
            long diff = ifSwitched.get(1) - ifSwitched.get(0);
            long secdiff = diff/1000;
            String timeSec = String.valueOf(secdiff);
            Log.d("aaa", timeSec);

            if (secdiff > 2) {

                ifSwitched.clear();
                System.out.println("różnica "+ secdiff+ ", lista wyczyszczona");

            }



        }

        else if (ifSwitched.size()==3){
           // String timeMillis = String.valueOf(timeMilli);
            long diff = ifSwitched.get(2) - ifSwitched.get(1);
            long secdiff = diff/1000;
            String timeSec = String.valueOf(secdiff);
            Log.d("aaa", timeSec);

            if (secdiff > 2) {

                ifSwitched.clear();
                System.out.println("różnica "+ secdiff+ ", lista wyczyszczona");

            }


        }

        else if (ifSwitched.size()==4){
            // String timeMillis = String.valueOf(timeMilli);
            long diff = ifSwitched.get(3) - ifSwitched.get(2);
            long secdiff = diff/1000;
            String timeSec = String.valueOf(secdiff);
            Log.d("aaa", timeSec);

            if (secdiff > 2) {

                ifSwitched.clear();
                System.out.println("różnica "+ secdiff+ ", lista wyczyszczona");

            }


        }


            if (ifSwitched.size() == 4) {
                long diff = ifSwitched.get(3) - ifSwitched.get(0);
                long secdiff = diff/1000;
                String timeSec = String.valueOf(secdiff);

                Log.d("aaa", timeSec);

                if (secdiff < 10) {


                    sendSMSMessage();

                    playTune();

                    //mainActivity.sendSMSMessage();
                    ifSwitched.clear();
                }

                else  ifSwitched.clear();

            }



//        Date date = new Date();
//        //This method returns the time in millis
//        long timeMilli = date.getTime();
//        System.out.println("Time in milliseconds using Date class: " + timeMilli);
//
//        System.out.println("rozmiar listy to" +ifSwitched.size());
//        ifSwitched.add(timeMilli);
//
//        String timeMillis = String.valueOf(timeMilli);
//
//        Log.d("aaa", timeMillis);
//
//        if (ifSwitched.size()>1)
//
//        if (ifSwitched.size() > 4) {
//            long diff = ifSwitched.get(4) - ifSwitched.get(0);
//            long secdiff = diff/1000;
//            String timeSec = String.valueOf(secdiff);
//
//            Log.d("aaa", timeSec);
//
//            if (secdiff < 30) {
//
//
//                sendSMSMessage();
//
//                //mainActivity.sendSMSMessage();
//                ifSwitched.clear();
//            }
//
//            else  ifSwitched.clear();
//
//        }

    }

}