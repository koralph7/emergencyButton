package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ScreenOnOffReceiver;
import com.example.myapplication.db.DBHelper;
import com.example.myapplication.mqttHelper.MqttHelper;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ScreenOnOffReceiver screenOnOffReceiver = null;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    TextView telNums;
    Button addNumBut, addProperNumBut;
    EditText telNumEdit;

    MqttHelper mqttHelper;

    Button btn;
    private ScreenOnOffReceiver myBR = null;

    TextView androidIdId;

    Intent mServiceIntent;
    private ScreenOnOffBackgroundService service;

    MediaPlayer mp;

    static String MQTTHOST = "tcp://83664-1-3e8237-01.services.oktawave.com:1883";
    static MqttAndroidClient client = null;

    TextView ifConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(this, R.raw.bbb);

        setTitle("dev2qa.com - Keep BroadcastReceiver Running After App Exit.");

        btn = findViewById(R.id.delete);

        telNumEdit = findViewById(R.id.addNumEdit);

        addNumBut = findViewById(R.id.addNumBut);

        addProperNumBut = findViewById(R.id.addNProperBut);

        //ifConnected = findViewById(R.id.ifConnected);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromDB();
            }
        });



        service = new ScreenOnOffBackgroundService();

        mServiceIntent = new Intent(this, service.getClass());
        if (!isMyServiceRunning(service.getClass())) {
            startService(mServiceIntent);
        }
        



        myBR = new ScreenOnOffReceiver();
        myBR.setMainActivityHandler(this);
        IntentFilter callInterceptorIntentFilter = new           IntentFilter("android.intent.action.ANY_ACTION");
        registerReceiver(myBR,  callInterceptorIntentFilter);

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               playTune();
//            }
//        });

       // androidIdId = findViewById(R.id.androidId);

        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), "android_id");

        //androidIdId.setText(deviceId);

        Log.d("bla", "onCreate: screenOnOffReceiver is registered.");

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        addNumBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telNumEdit.setVisibility(View.VISIBLE);
                addProperNumBut.setVisibility(View.VISIBLE);
                addNumBut.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
            }
        });

        addProperNumBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (telNumEdit!=null){
                    addTelNumberToDb(telNumEdit.getText().toString());
                }

                showNums();
                btn.setVisibility(View.VISIBLE);
                addNumBut.setVisibility(View.VISIBLE);
                telNumEdit.setVisibility(View.GONE);
                addProperNumBut.setVisibility(View.GONE);


            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }



        showNums();
        mqttSet();


    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void addTelNumberToDb(String s) {
        DBHelper dbHelperr = new DBHelper(MainActivity.this);

        if (!dbHelperr.getAll().contains(s)){
            dbHelperr.addOne(s);
        }

//        if (!Arrays.asList(dbHelperr.getAll()).contains(s)) {
//            dbHelperr.addOne(s);
//        }
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }



public void showNums(){
    final DBHelper dbHelper = new DBHelper(MainActivity.this);


    telNums = findViewById(R.id.telNums);

    telNums.setText("nie ma jeszcze numerów");

    List<String> newOne = dbHelper.getAll();




   telNums.setText(newOne.toString());
}


    @NonNull
    private DisconnectedBufferOptions getDisconnectedBufferOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }

    public void deleteFromDB(){
        DBHelper dbHelperr = new DBHelper(MainActivity.this);

        dbHelperr.deleteTable();
        telNums.setText("");
    }

//    public void setMqtt(){
//        String generateClientId = MqttClient.generateClientId();
//        client = new MqttAndroidClient(getApplicationContext(), MQTTHOST, generateClientId);
//        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//
//        try {
//            IMqttToken connect = client.connect(mqttConnectOptions);
//            mqttConnectOptions.setKeepAliveInterval(30);
//            mqttConnectOptions.setAutomaticReconnect(true);
//            mqttConnectOptions.setCleanSession(false);
//            client.setCallback(new MqttCallback() {
//                @Override
//                public void connectionLost(Throwable cause) {
//                    Log.i("TAG", "connection lost");
//                }
//
//                @Override
//                public void messageArrived(String totopic, MqttMessage message) throws Exception {
//                    //Log.i("TAG", "topic: " + "didi" + ", msg: " + new String(message.getPayload()));
//                    //textView.setText("mqttIn "+new String(message.getPayload()));
//                    addTelNumberToDb(new String(message.getPayload()));
//                }
//
//                @Override
//                public void deliveryComplete(IMqttDeliveryToken token) {
//                    Log.i("TAG", "msg delivered");
//                }
//            });
//            connect.setActionCallback(new IMqttActionListener() {
//                                          public void onSuccess(IMqttToken iMqttToken) {
//                                              Toast.makeText(MainActivity.this, "Jesteś połączony", Toast.LENGTH_LONG).show();
//                                              subscribeToTopic();
//                                              MainActivity.client.setBufferOpts(MainActivity.this.getDisconnectedBufferOptions());
//                                              MainActivity.this.ifConnected.setText("connected");
//
//                                          }
//
//                                          public void onFailure(IMqttToken iMqttToken, Throwable th) {
//                                              Toast.makeText(MainActivity.this, "O nie, nie masz połączenia", Toast.LENGTH_LONG).show();
//                                              MainActivity.this.ifConnected.setText("Not connected");
//                                          }
//                                      }
//
//
//            );
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }

    private void mqttSet(){
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
//                MainActivity.this.ifConnected.setText("connected");
            }

            @Override
            public void connectionLost(Throwable cause) {
//                MainActivity.this.ifConnected.setText("Not connected");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                addTelNumberToDb(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void playTune(){
        MediaPlayer mp;
        mp = MediaPlayer.create(this, R.raw.bbb);
        mp.start();
    }

    private void subscribeToTopic() {
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), "android_id");
String topicc = deviceId+"aa";
        try {
            client.subscribe(topicc, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "succeded in subscribin",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Failed in subscribin",
                            Toast.LENGTH_LONG).show();
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {


        // Unregister screenOnOffReceiver when destroy.
        if(screenOnOffReceiver!=null)
        {
            unregisterReceiver(screenOnOffReceiver);

            Log.d( "bla","onDestroy: screenOnOffReceiver is unregistered.");
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, CallBroadcastReceiver.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();

        //startService(new Intent(this, ScreenOnOffBackgroundService.class));
    }

    public void sendToast(){
        Toast.makeText(this, "sms was sent", Toast.LENGTH_LONG).show();
    }



    public void sendSMSMessage() {
//        phoneNo = txtphoneNo.getText().toString();
//        message = txtMessage.getText().toString();

        //Toast.makeText(this, "balbala", Toast.LENGTH_LONG).show();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("668880445", null, "Button Panic's working", null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("668880445", null, "blablabla", null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }



//
//    protected void sendSMSMessage() {
//
//
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.SEND_SMS)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.SEND_SMS)) {
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_SMS);
//            }
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage("668880445", null, "Wysyłanie sms ", null, null);
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
}