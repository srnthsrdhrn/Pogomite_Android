package io.iqube.pomogite;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

import io.iqube.pomogite.Models.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.internal.SharedRealm;

public class PomogiteApplication extends Application {
    public static String ROOT_TOPIC = "srinath/4441848919/";
    public static String passive_topic = "";
    public static String location_publish_topic = "";
    public static String customer_order_update = "";
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Fresco.initialize(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        User user = PomogiteApplication.GetUser(this);
        passive_topic = ROOT_TOPIC + user.getMqtt_token() + "/passive";
        location_publish_topic = ROOT_TOPIC+ user.getMqtt_token()+"/location";
        customer_order_update = ROOT_TOPIC+user.getMqtt_token()+"/customer_order_update";
        Log.i("pomogite_application",location_publish_topic);
        Log.i("pomogite_application",passive_topic);

    }

    public static void StoreUser(User user, String password, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", user.getUsername());
        editor.putString("password", password);
        editor.putString("first_name", user.getFirstName());
        editor.putInt("id", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("address", user.getAddress());
        editor.putString("mobile_number", user.getMobileNumber());
        editor.putString("lat", user.getLat());
        editor.putString("lng", user.getLng());
        editor.putString("mqtt_token",user.getMqtt_token());
        editor.putBoolean("is_offering_service",user.getIs_offering_service());
        editor.apply();
    }

    public static User GetUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
        String username = preferences.getString("username", null);
        String password = preferences.getString("password", null);
        String first_name = preferences.getString("first_name", null);
        String mqtt_token = preferences.getString("mqtt_token",null);
        int id = preferences.getInt("id", 1);
        String email = preferences.getString("email", null);
        String address = preferences.getString("address", null);
        String mobile_number = preferences.getString("mobile_number", null);
        String lat = preferences.getString("lat", null);
        String lng = preferences.getString("lng", null);
        Boolean is_offering_service = preferences.getBoolean("is_offering_service",false);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(first_name);
        user.setId(id);
        user.setEmail(email);
        user.setAddress(address);
        user.setMobileNumber(mobile_number);
        user.setLat(lat);
        user.setLng(lng);
        user.setMqtt_token(mqtt_token);
        user.setIs_offering_service(is_offering_service);
        return user;

    }

    public static void DeleteUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

    }
}
