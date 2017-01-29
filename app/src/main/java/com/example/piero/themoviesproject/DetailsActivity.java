package com.example.piero.themoviesproject;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import utilities.Movie;
import utilities.NetworkUtils;


public class DetailsActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private TextView mOverviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = (ImageView) findViewById(R.id.iv_thumbnail);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);

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
                    .centerInside()
                    .fit()
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
