package io.iqube.pomogite.HireService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.List;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.iqube.pomogite.Models.Service;
import io.iqube.pomogite.Models.User;
import io.iqube.pomogite.Network.APIClient;
import io.iqube.pomogite.Network.ServiceGenerator;
import io.iqube.pomogite.PomogiteApplication;
import io.iqube.pomogite.R;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceList extends Fragment {
    RealmRecyclerView realmRecyclerView;
    ServiceListRecyclerViewAdapter adapter;
    FloatingActionButton fab;

    public ServiceList() {
        // Required empty public constructor
    }


    public static ServiceList newInstance() {
        ServiceList fragment = new ServiceList();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_service_list, container, false);
        fetchServices();
        fab = v.findViewById(R.id.add_service);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.add_service, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setView(v)
                        .setTitle("Add Service");
                final Spinner spinner = v.findViewById(R.id.service_type);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, new String[]{
                        "Hourly",
                        "Weekly",
                        "Monthly",
                        "Yearly"
                });

                spinner.setAdapter(adapter);
                final EditText service_name, service_fare, service_description;
                service_name = v.findViewById(R.id.service_name);
                service_fare = v.findViewById(R.id.service_fare);
                service_description = v.findViewById(R.id.service_description);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        String type = (String) spinner.getSelectedItem();
                        String name = service_name.getText().toString();
                        double fare = Double.parseDouble(service_fare.getText().toString());
                        String description = service_description.getText().toString();
                        User user = PomogiteApplication.GetUser(getContext());
                        APIClient client = ServiceGenerator.createService(APIClient.class, user.getUsername(), user.getPassword());
                        JsonObject data = new JsonObject();
                        data.addProperty("type", type);
                        data.addProperty("fare", fare);
                        data.addProperty("description", description);
                        data.addProperty("name", name);
                        data.addProperty("user_id", user.getId());
                        client.createService(data).enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Call<Object> call, Response<Object> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Service created successfully", Toast.LENGTH_SHORT).show();
                                    fetchServices();
                                } else {
                                    Toast.makeText(getContext(), "Error creating Service", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable t) {
                                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        realmRecyclerView = v.findViewById(R.id.service_list_recycler_view);
        final Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        if (user != null) {
            APIClient client = ServiceGenerator.createService(APIClient.class, user.getUsername(), user.getPassword());
            client.getServices().enqueue(new Callback<List<Service>>() {
                @Override
                public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                    if (response.isSuccessful()) {
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(response.body());
                        realm.commitTransaction();
                        adapter.notifyDataSetChanged();


                    }
                }

                @Override
                public void onFailure(Call<List<Service>> call, Throwable t) {

                }
            });
        }
        RealmResults<Service> results = realm.where(Service.class).findAll();
        adapter = new ServiceListRecyclerViewAdapter(getContext(), results);
        realmRecyclerView.setAdapter(adapter);
        realmRecyclerView.setOnRefreshListener(new RealmRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchServices();
            }
        });
        return v;

    }

    public void fetchServices() {
        final Realm realm = Realm.getDefaultInstance();
        User user = PomogiteApplication.GetUser(getContext());
        if (user != null) {
            APIClient client = ServiceGenerator.createService(APIClient.class, user.getUsername(), user.getPassword());
            client.getServices().enqueue(new Callback<List<Service>>() {
                @Override
                public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                    if (response.isSuccessful()) {
                        realm.beginTransaction();
                        realm.delete(Service.class);
                        realm.copyToRealmOrUpdate(response.body());
                        realm.commitTransaction();
                        adapter.notifyDataSetChanged();
                        realmRecyclerView.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<List<Service>> call, Throwable t) {
                    realmRecyclerView.setRefreshing(false);
                    Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Data Corruption. Login Again", Toast.LENGTH_SHORT).show();
            realmRecyclerView.setRefreshing(false);
        }
    }
}
