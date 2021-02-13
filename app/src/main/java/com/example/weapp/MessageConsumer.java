package com.example.weapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

public class MessageConsumer extends IntentService implements DeliverCallback {

    public MessageConsumer() {
        super("MessageConsumer");

    }



    @Override
    public void handle(String consumerTag, Delivery message) throws IOException {
        String result = new String(message.getBody(), "UTF-8");
        System.out.println(" [x] Received '" + result + "'");
        try {
            JSONObject msg=new JSONObject(result);
            String bodyItem = (String) msg.get("body");
            System.out.println(" [x] bodyItem '" + bodyItem + "'");

            JSONArray numbersItem = (JSONArray) msg.get("numbers");
            System.out.println(" [x] bodyItem '" + numbersItem.get(0) +"'");

            for(int i=0; i < numbersItem.length(); i++){
                Thread.sleep(2000);
                sendBroadcastWhatsappPayload((String) numbersItem.get(i),bodyItem);
              //  Thread.sleep(5000);
            }

        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SMS = "com.example.weapp.action.SMS";
    private static final String ACTION_WHATSAPP = "com.example.weapp.action.WHATSAPP";
    // TODO: Rename parameters
    private static final String MESSAGE = "com.example.weapp.extra.PARAM1";
    private static final String MOBILE_NUMBER = "com.example.weapp.extra.PARAM3";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSMS(Context context, String message, String mobile_number) {
        Intent intent = new Intent(context, MySMSService.class);
        intent.setAction(ACTION_SMS);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(MOBILE_NUMBER, mobile_number);
        context.startService(intent);
    }



    public static void startActionWhatsapp(Context context, String message,String mobile_number) {
        Intent intent = new Intent(context, MessageConsumer.class);
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
            String mobile = "212611568681";
            String msg = "Its Working   ";
            String url = "https://api.whatsapp.com/send?phone="+numero+"&text="+ URLEncoder.encode(message,"utf-8");
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
        }
    }

    private void sendBroadcastMessage(String message ){
        Intent localIntent = new Intent("my.own.broadcast");
        localIntent.putExtra("result",message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void sendBroadcastWhatsappPayload(String number,String body){
        Intent localIntent = new Intent("my.whatsapp.broadcast");
        localIntent.putExtra("number",number);
        localIntent.putExtra("body",body);
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
                final String message = intent.getStringExtra(MESSAGE);
                final String number = intent.getStringExtra(MOBILE_NUMBER);
                handleWhatsappAction(message, number);
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
