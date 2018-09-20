package io.iqube.pomogite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.ramotion.circlemenu.CircleMenuView;

import io.iqube.pomogite.HireService.ServiceList;
import io.iqube.pomogite.Home.LandingPage;
import io.iqube.pomogite.Location.LocationService;
import io.iqube.pomogite.Models.User;
import io.realm.Realm;

public class HomeActivity extends AppCompatActivity {
    CircleMenuView menu;
    boolean flag = false;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        menu = findViewById(R.id.circle_menu);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("HomeActivity");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.background_light));
        setSupportActionBar(toolbar);
        startService(new Intent(this, MyMqttService.class));
        Fragment f = LandingPage.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
        toolbar.setNavigationIcon(R.drawable.ic_navigation_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    menu.close(true);
                    flag = false;
                } else {
                    menu.open(true);
                    flag = true;
                }
            }
        });
        menu.setEventListener(new CircleMenuView.EventListener() {
            @Override
            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
                super.onMenuOpenAnimationStart(view);
                menu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView view) {
                super.onMenuCloseAnimationEnd(view);
                menu.setVisibility(View.GONE);
                flag = false;
            }

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int buttonIndex) {
                super.onButtonClickAnimationStart(view, buttonIndex);
                menu.setVisibility(View.GONE);
                flag = false;
                switch (buttonIndex) {
                    case 0:
                        Fragment f = ServiceList.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
                        break;
                    case 1:
                        Fragment a = SettingsFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, a).commit();
                        break;
                    case 2:
                        Toast.makeText(HomeActivity.this, "2", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.deleteAll();
                        realm.commitTransaction();
                        PrefManager prefManager = new PrefManager(HomeActivity.this);
                        prefManager.setFirstTimeLaunch(true);
                        PomogiteApplication.DeleteUser(HomeActivity.this);
                        finish();
                        startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
                        Toast.makeText(HomeActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                }
            }

        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO:
            // Consider calling  ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }
        User user = PomogiteApplication.GetUser(this);
        if (user.getIs_offering_service()) {
            startService(new Intent(this, LocationService.class));
        }
        turnGPSOn();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            User user = PomogiteApplication.GetUser(this);
            if (user.getIs_offering_service()) {
                startService(new Intent(this, LocationService.class));
            }
        }
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
}
