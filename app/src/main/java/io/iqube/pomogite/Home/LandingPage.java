package io.iqube.pomogite.Home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;

import java.util.List;

import io.iqube.pomogite.Models.Slider;
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


public class LandingPage extends Fragment {
    SliderLayout slider;

    public LandingPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LandingPage.
     */
    // TODO: Rename and change types and number of parameters
    public static LandingPage newInstance() {
        return new LandingPage();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_landing_page, container, false);
        slider = v.findViewById(R.id.slider);
        slider.setPresetTransformer(SliderLayout.Transformer.Stack);
        slider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(3000);
        User user = PomogiteApplication.GetUser(getContext());
        APIClient client = ServiceGenerator.createService(APIClient.class, user.getUsername(), user.getPassword());
        client.getSlides().enqueue(new Callback<List<Slider>>() {
            @Override
            public void onResponse(Call<List<Slider>> call, Response<List<Slider>> response) {
                if (response.isSuccessful()) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(response.body());
                    realm.commitTransaction();
                    RealmResults<Slider> results = realm.where(Slider.class).findAll();
                    for (int i = 0; i < results.size(); i++) {
                        Slider slider1 = results.get(i);
                        TextSliderView view = new TextSliderView(getContext());
                        view.description(slider1.getText()).image(ServiceGenerator.BASE_URL + slider1.getImage());
                        slider.addSlider(view);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Slider>> call, Throwable t) {

            }
        });


        return v;

    }

}
