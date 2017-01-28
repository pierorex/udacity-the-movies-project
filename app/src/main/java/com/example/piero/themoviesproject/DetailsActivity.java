package com.example.piero.themoviesproject;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import utilities.NetworkUtils;


public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView imageView = (ImageView) findViewById(R.id.iv_thumbnail);

        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            Picasso.with(DetailsActivity.this)
                    .load(NetworkUtils.buildPosterUrl(ListActivity.movies[position]))
                    //.placeholder(R.raw.place_holder)
                    .noFade()
                    //.error(R.raw.big_problem)
                    .into(imageView);

        }

//        } else {
//            Picasso.with(DetailsActivity.this)
//                    .load(R.raw.big_problem)
//                    .noFade()
//                    .resize(800, 800)
//                    .centerCrop()
//                    .into(imageView);
//        }
    }
}
