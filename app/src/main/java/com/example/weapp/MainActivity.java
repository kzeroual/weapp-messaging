package com.example.weapp;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
    Button btn_whatsapp;
    Button btn_sms;
    String QUEUE_NAME = "send_messages";
    String RABBIT_HOST = "13.59.162.164";
    String RABBIT_USER = "admin";
    String RABBIT_PASS = "admin";
    ConnectionFactory factory = new ConnectionFactory();
    RabbitConnection rabbitConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rabbitConnection= new RabbitConnection(getApplicationContext());
        rabbitConnection.execute();
        btn_whatsapp = findViewById(R.id.btn);
        btn_sms = findViewById(R.id.btnSMS);
        final String num = "+212611568681";
        final String text = "Hello";
        checkPermission();

        if(!isAccessibilityOn(getApplicationContext())){
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySMSService.startActionWhatsapp(getApplicationContext(),"salam","212665712903");
            }
        });
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySMSService.startActionSMS(
                        getApplicationContext(),
                        "212665712903",
                        "salam"
                );
            }
        });
        IntentFilter intentFilter = new IntentFilter("my.own.broadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(receivedBroadcast,intentFilter);

        IntentFilter intentWhatsappFilter = new IntentFilter("my.whatsapp.broadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(receivedWhatsappBroadcast,intentWhatsappFilter);
    }


    private BroadcastReceiver receivedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             String result = intent.getStringExtra("result");
            synchronized (System.out) {

            }
            Toast.makeText(MainActivity.this,result,Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver receivedWhatsappBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra("number");
            String body = intent.getStringExtra("body");
            MySMSService.startActionWhatsapp(getApplicationContext(),body,number);
            synchronized (System.out) {

            }

         //   MessageConsumer.startActionWhatsapp(getApplicationContext(),body,number);
        }
    };
    private void checkPermission(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
        Dexter.withContext(this).withPermission(Manifest.permission.INTERNET)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }
    private void sendMessageViaWhatsapp() {
        PackageManager pm=getPackageManager();
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            String toNumber = "212611568681";
            String sMessage = "salam";
            sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
            sendIntent.putExtra(Intent.EXTRA_TEXT, sMessage);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch ( Exception e) {
            //error message
        }
    }
    private boolean isAppInstalled(String s) {
        PackageManager packageManager = getPackageManager();
        boolean is_installed;

        try {
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            is_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            is_installed = false;
            e.printStackTrace();
        }
        return is_installed;
    }


    private boolean isAccessibilityOn(Context context) {
     int accessibilityEnabled = 0;
     final String service = context.getPackageName()+"/"+ WhatsappAccessibilityService.class.getCanonicalName();
     try {
         if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED) != 0) {
             return true;
         }
         return false;
     }catch (Exception e){
         return false;
     }
    }
}