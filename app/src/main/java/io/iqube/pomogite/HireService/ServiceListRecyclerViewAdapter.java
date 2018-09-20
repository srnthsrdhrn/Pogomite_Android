package io.iqube.pomogite.HireService;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ramotion.foldingcell.FoldingCell;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.iqube.pomogite.Models.Service;
import io.iqube.pomogite.Models.User;
import io.iqube.pomogite.Network.APIClient;
import io.iqube.pomogite.Network.ServiceGenerator;
import io.iqube.pomogite.PomogiteApplication;
import io.iqube.pomogite.R;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceListRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<Service, ServiceListRecyclerViewAdapter.ViewHolder> {

    Context context;

    public ServiceListRecyclerViewAdapter(Context context,
                                          RealmResults<Service> realmResults) {
        super(context, realmResults, true, false);
        this.context = context;
    }


    class ViewHolder extends RealmViewHolder {


        TextView title_exp, title, price_exp, price, description, type_exp, type, username;
        SimpleDraweeView image;
        Button hire;

        ViewHolder(CardView container) {
            super(container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final FoldingCell foldingCell = view.findViewById(R.id.folding_cell);
                    foldingCell.toggle(false);
                }
            });
            title = container.findViewById(R.id.service_name);
            type = container.findViewById(R.id.service_type);
            price = container.findViewById(R.id.service_fare);

            title_exp = container.findViewById(R.id.service_name_exp);
            price_exp = container.findViewById(R.id.service_fare_exp);
            type_exp = container.findViewById(R.id.service_type_exp);
            description = container.findViewById(R.id.service_description_exp);
            image = container.findViewById(R.id.service_image_exp);
            username = container.findViewById(R.id.username_exp);
            hire = container.findViewById(R.id.service_hire_exp);

        }

    }


    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.service_foldable_cell, viewGroup, false);

        return new ViewHolder((CardView) v);
    }


    @Override
    public void onBindRealmViewHolder(ViewHolder holder, int position) {
        final Service service = realmResults.get(position);
        holder.title_exp.setText(service.getName());
        holder.type_exp.setText(service.getType());
        holder.price_exp.setText(service.getFare() + "");
        holder.title.setText(service.getName());
        holder.description.setText(service.getDescription());
        holder.price.setText(service.getFare() + "");
        holder.username.setText(service.getUser().getFirstName());
        holder.type.setText(service.getType());
        Uri imageUri = Uri.parse(ServiceGenerator.BASE_URL + service.getUser().getProfilePic());
        holder.image.setImageURI(imageUri);
        holder.hire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar now = Calendar.getInstance();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N
                        ) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context);
                    final TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            now.set(Calendar.HOUR,i);
                            now.set(Calendar.MINUTE,i1);
                            long time = now.getTimeInMillis();
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy  hh:mm:ss", Locale.US);
                            String date = format.format(time);
                            User user = PomogiteApplication.GetUser(context);
                            APIClient client = ServiceGenerator.createService(APIClient.class, user.getUsername(), user.getPassword());
                            JsonObject data = new JsonObject();

                            data.addProperty("customer_id", user.getId());
                            data.addProperty("worker_id", service.getUser().getId());
                            data.addProperty("startDateTime", date);
                            JsonArray array = new JsonArray();
                            array.add(service.getId());
                            data.add("services", array);
                            client.createOrder(data).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    Toast.makeText(context, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    },0,0,true);

                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            now.set(i2,i1,i);
                            timePicker.show();
                        }
                    });
                    datePickerDialog.show();


                }

            }
        });
    }


}
