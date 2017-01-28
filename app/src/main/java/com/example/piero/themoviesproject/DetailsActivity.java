package com.example.piero.themoviesproject;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import utilities.Movie;
import utilities.NetworkUtils;


public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView imageView = (ImageView) findViewById(R.id.iv_thumbnail);
        TextView mTitleTextView = (TextView) findViewById(R.id.tv_title);
        TextView mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        TextView mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        TextView mOverviewTextView = (TextView) findViewById(R.id.tv_overview);

        int position = getIntent().getIntExtra("position", -1);
        Movie movie = ListActivity.movies[position];

        if (position != -1) {
            mTitleTextView.setText(movie.title);
            mReleaseDateTextView.setText(movie.formatedReleaseDate());
            mVoteAverageTextView.setText(movie.formatedVoteAverage());
            mOverviewTextView.setText(movie.overview);

            Picasso.with(DetailsActivity.this)
                    .load(NetworkUtils.buildPosterUrl(movie))
                    .error(R.raw.load_error)
                    .noFade()
                    .resize(500, 0)
                    .into(imageView);
        } else {
            Picasso.with(DetailsActivity.this)
                    .load(R.raw.load_error)
                    .noFade()
                    .resize(500, 0)
                    .into(imageView);
        }
    }
}
