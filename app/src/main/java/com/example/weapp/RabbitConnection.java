package com.example.weapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RabbitConnection extends  AsyncTask <String,Void,Void> {
    String QUEUE_NAME = "send_messages";
    String RABBIT_HOST = "13.59.162.164";
    String RABBIT_USER = "admin";
    String RABBIT_PASS = "admin";
    ConnectionFactory factory = new ConnectionFactory();
    Connection connection;
    Channel channel;
    Context context;
    @Override
    protected Void doInBackground(String... strings) {
        this.init();
        return null;
    }

    public RabbitConnection(Context context){
        this.context = context;
    }
    private void init() {
        try {
            factory.setHost(RABBIT_HOST);
            factory.setUsername(RABBIT_USER);
            factory.setPassword(RABBIT_PASS);
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Example3";
           // channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            listenToQueue();
            System.out.println(" [x] Sent '" + message + "'");

        } catch ( Exception e) {
            throw new RuntimeException("Rabbitmq problem", e);
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public Connection getConnection() {
        return connection;
    }

    public void listenToQueue() throws IOException {
        MessageConsumer consumer = new MessageConsumer();
        channel.basicConsume(QUEUE_NAME, true, consumer, consumerTag -> { });
      /*  boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, "myConsumerTag",
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException
                    {
                        String routingKey = envelope.getRoutingKey();
                        String contentType = properties.getContentType();
                        long deliveryTag = envelope.getDeliveryTag();
                        try {
                            JSONObject msg=new JSONObject(new String(body));
                            Toast.makeText(context,msg.toString(),Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(context,"error".toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        // (process the message components here ...)
                      //  channel.basicAck(deliveryTag, false);
                    }
                });*/
    }

}
