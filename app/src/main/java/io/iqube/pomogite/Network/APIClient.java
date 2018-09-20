package io.iqube.pomogite.Network;

import com.google.gson.JsonObject;


import org.json.JSONObject;

import java.util.List;

import io.iqube.pomogite.Models.Service;
import io.iqube.pomogite.Models.Slider;
import io.iqube.pomogite.Models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIClient {
    @FormUrlEncoded
    @POST("login/")
    Call<User> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("register/")
    Call<JsonObject> register(@Field("username") String username,
                          @Field("password") String password,
                          @Field("email") String email,
                          @Field("first_name") String first_name,
                          @Field("last_name") String last_name,
                          @Field("mobile_number") String mobile_number);

    @GET("slides")
    Call<List<Slider>> getSlides();

    @GET("service")
    Call<List<Service>> getServices();

    @POST("service/")
    Call<Object> createService(@Body JsonObject data);

    @POST("create_order")
    Call<JsonObject> createOrder(@Body JsonObject data);

    @POST("save_settings")
    Call<Object> saveSettings(@Body JsonObject data);

}