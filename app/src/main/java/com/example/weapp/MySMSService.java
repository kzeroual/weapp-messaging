package com.example.weapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.net.URLEncoder;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MySMSService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SMS = "com.example.weapp.action.SMS";
    private static final String ACTION_WHATSAPP = "com.example.weapp.action.WHATSAPP";
    // TODO: Rename parameters
    private static final String MESSAGE = "com.example.weapp.extra.PARAM1";
    private static final String MOBILE_NUMBER = "com.example.weapp.extra.PARAM3";

    public MySMSService() {
        super("MySMSService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSMS(Context context, String message,String mobile_number) {
        Intent intent = new Intent(context, MySMSService.class);
        intent.setAction(ACTION_SMS);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(MOBILE_NUMBER, mobile_number);
        context.startService(intent);
    }

    public static void startActionWhatsapp(Context context, String message,String mobile_number) {
        Intent intent = new Intent(context, MySMSService.class);
        intent.setAction(ACTION_WHATSAPP);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(MOBILE_NUMBER, mobile_number);
        context.startService(intent);
    }

    private void handleSmsAction(String message,String numero) {
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("212665712903",null,"salam",null,null);
            sendBroadcastMessage("sent SMS");
            // Toast.makeText(MainActivity.this,"sent SMS",Toast.LENGTH_LONG).show();
        }catch(Exception e){
            sendBroadcastMessage("cannot send SMS "+e.getMessage());
            // Toast.makeText(MainActivity.this,"cannot send SMS "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void handleWhatsappAction(String message,String numero) {
        try{
            PackageManager packageManager = getApplicationContext().getPackageManager();
            String mobile = numero;
            String msg = "Its Working   ";
            msg=message + "   ";
            String url = "https://api.whatsapp.com/send?phone="+mobile+"&text="+ URLEncoder.encode(msg,"utf-8");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            if(i.resolveActivity(packageManager)!=null){
                getApplicationContext(). startActivity(i);
                //;
                Thread.sleep(5000);
                sendBroadcastMessage("Message is sent");
            } else {
                sendBroadcastMessage("cannot sent a message");
            }
        }catch(Exception e){
            sendBroadcastMessage("whatsapp not installed");
            // Toast.makeText(MainActivity.this,"cannot send SMS "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void sendBroadcastMessage(String message ){
        Intent localIntent = new Intent("my.own.broadcast");
        localIntent.putExtra("result",message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MySMSService.class);
        // intent.setAction(ACTION_BAZ);
        //  intent.putExtra(EXTRA_PARAM1, param1);
        //  intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final String action = intent.getAction();
            if (ACTION_SMS.equals(action)) {
                final String param1 = intent.getStringExtra(MESSAGE);
                final String param2 = intent.getStringExtra(MOBILE_NUMBER);
                handleSmsAction(param1, param2);
            }
            else if (ACTION_WHATSAPP.equals(action)) {
                final String param1 = intent.getStringExtra(MESSAGE);
                final String param2 = intent.getStringExtra(MOBILE_NUMBER);
                handleWhatsappAction(param1, param2);
            }

        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}