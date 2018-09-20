package io.iqube.pomogite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.JsonObject;

import io.iqube.pomogite.Location.LocationService;
import io.iqube.pomogite.Models.User;
import io.iqube.pomogite.Network.APIClient;
import io.iqube.pomogite.Network.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingsFragment extends Fragment {
     public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
    boolean offer_service_flag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_settings, container, false);
        final Switch offer_service = v.findViewById(R.id.offer_service);
        offer_service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                offer_service_flag = b;
            }
        });
        Button save = v.findViewById(R.id.save);
        final User user  = PomogiteApplication.GetUser(getContext());
        final APIClient client = ServiceGenerator.createService(APIClient.class,user.getUsername(),user.getPassword());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JsonObject data = new JsonObject();
                data.addProperty("offer_service",offer_service_flag);
                data.addProperty("user_id",user.getId());
                client.saveSettings(data).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(offer_service_flag) {
                            getContext().startService(new Intent(getContext(), LocationService.class));
                        }else{
                            getContext().stopService(new Intent(getContext(),LocationService.class));
                        }
                        Toast.makeText(getContext(), "SettingsFragment Saved Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d("SettingsFragment",t.getMessage());
                        Toast.makeText(getContext(), "Error Saving SettingsFragment", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return v;
    }


}
