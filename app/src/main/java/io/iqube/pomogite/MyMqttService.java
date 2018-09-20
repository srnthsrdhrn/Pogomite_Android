package io.iqube.pomogite;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static io.iqube.pomogite.Location.LocationService.date_new;
import static io.iqube.pomogite.Location.LocationService.latitude;
import static io.iqube.pomogite.Location.LocationService.longitude;
import static io.iqube.pomogite.Location.LocationService.speed;
import static io.iqube.pomogite.PomogiteApplication.location_publish_topic;
import static io.iqube.pomogite.PomogiteApplication.passive_topic;

public class MyMqttService extends Service {
    String TAG = "Mqtt_Service";
    public MqttAndroidClient client;
    private Handler mHandler;
    public static double lat, lng;
    public static String username = "";
    public String worker_topic;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        client = createMqttAndroidClient();
        connect(client);
        mHandler = new Handler();
        return START_STICKY;
    }

    final Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            JSONObject object = new JSONObject();
            try {
                object.put("latitude", latitude);
                object.put("longitude", longitude);
                object.put("speed", speed);
                object.put("date", date_new);
                try {
                    publish(location_publish_topic, object.toString());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mHandler.postDelayed(mStatusChecker, 5000);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private MqttAndroidClient createMqttAndroidClient() {
        if (client == null) {
            String clientId = MqttClient.generateClientId();
            client =
                    new MqttAndroidClient(this, "tcp://broker.hivemq.com:1883",
                            clientId);

        }
        return client;

    }

    public void connect(final MqttAndroidClient client) {

        try {
            if (!client.isConnected()) {
                IMqttToken token = client.connect();
                //on successful connection, publish or subscribe as usual
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("Connection", "Success");
                        try {
                            subscribe();
                            mStatusChecker.run();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("Connection", "Failure");
                    }
                });
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String payload = String.valueOf(message.getPayload());
                        JSONObject data = new JSONObject(payload);
                        if (topic.equals(passive_topic)) {
                            if (data.get("code") == "USER_REGISTER") {
                                double lat = data.getDouble("lat");
                                double lng = data.getDouble("lng");
                                String username = data.getString("username");
                                String ServiceStartTime = data.getString("time");

                            } else if (data.get("code") == "CUSTOMER_USER_UPDATE") {
                                String worker_topic = data.getString("worker_topic");
                                client.subscribe(worker_topic, 1);
                                sendNotification("Your Worker is on the way", "Track your worker");
                            }
                        } else if (topic.equals(worker_topic)) {
//                            {
//                                "username": "srinath",
//                                    "lat": null,
//                                    "lng": null,
//                                    "code": "USER_REGISTER",
//                                    "time": "02/09/2018 11:31:57"
//                            }
                            lat = data.getDouble("lat");
                            lng = data.getDouble("lng");
                            Toast.makeText(MyMqttService.this, lat + "/" + lng, Toast.LENGTH_SHORT).show();

                        }
                        Log.d("Message", Arrays.toString(message.getPayload()));

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
            }
        } catch (MqttException e) {
            //handle e
            e.printStackTrace();
        }
    }


    public boolean publish(String topic, String message) throws MqttException {
        if (client != null) {
            client.publish(topic, message.getBytes(), 1, false);
            return true;
        }
        Log.e("MQTT_Service", "Cant Publish without client connection");
        return false;
    }

    public void subscribe() throws MqttException {
        Log.d(TAG, passive_topic);
        int qos = 1;
        client.subscribe(passive_topic, qos, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("Subscribe", "Success");


            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("Subscribe", "Failure");
            }
        });
    }

    private void sendNotification(String message, String topic) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("open_worker_track", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(topic)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}

