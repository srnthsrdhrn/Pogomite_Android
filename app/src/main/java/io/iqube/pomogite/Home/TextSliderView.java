package io.iqube.pomogite.Home;

/**
 * Created by raja sudhan on 12/4/2016.
 */

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import io.iqube.pomogite.R;


/**
 * This is a slider with a description TextView.
 */
public class TextSliderView extends BaseSliderView {
    public TextSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.slider_home_fresco,null);
        SimpleDraweeView target = v.findViewById(R.id.slider_image);
        TextView description = v.findViewById(R.id.description);
        description.setText(getDescription());
        bindEventAndShow(v, target);
        return v;
    }

    @Override
    public Picasso getPicasso() {
        return super.getPicasso();
    }

    private void bindEventAndShow(final View v, SimpleDraweeView targetImageView) {
        final BaseSliderView me = this;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSliderClickListener != null) {
                    mOnSliderClickListener.onSliderClick(me);
                }
            }
        });

        if (targetImageView == null)
            return;

        Uri uri = Uri.parse(getUrl());
        targetImageView.setImageURI(uri);

        if (v.findViewById(R.id.loading_bar) != null) {
            v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
        }

    }


}