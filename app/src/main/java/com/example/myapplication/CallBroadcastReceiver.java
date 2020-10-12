//package com.example.myapplication;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//public class CallBroadcastReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Intent i = new Intent();
//        i.setClassName("com.example.myapplication",
//                "com.example.myapplication.MainActivity");
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
//    }
//}