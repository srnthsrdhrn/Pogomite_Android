package io.iqube.pomogite.Network;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    //    public static final String API_BASE_URL = "https://gday.iqube.io/api/";
    public static final String API_BASE_URL = "http://192.168.43.136:8000/api/v1/";
    public static final String BASE_URL = "http://192.168.43.136:8000";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass, final String username, final String password) {
        httpClient = httpClient.authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        });
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }


}