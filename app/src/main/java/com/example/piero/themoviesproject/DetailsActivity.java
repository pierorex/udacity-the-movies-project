package com.example.piero.themoviesproject;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piero.themoviesproject.utilities.JsonUtils;
import com.example.piero.themoviesproject.utilities.Trailer;
import com.squareup.picasso.Picasso;

import com.example.piero.themoviesproject.utilities.Movie;
import com.example.piero.themoviesproject.utilities.NetworkUtils;

import java.net.URL;


public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler {
    private static Trailer[] mTrailers;
    private ImageView mPoster, mBackdrop, mPlayButton;
    private TextView mTitleTextView, mReleaseDateTextView, mVoteAverageTextView, mOverviewTextView;
    private RecyclerView mRecyclerView;
    private TrailersAdapter mTrailersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPoster = (ImageView) findViewById(R.id.iv_poster);
        mBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        mPlayButton = (ImageView) findViewById(R.id.iv_play_button);

        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);

        int position = getIntent().getIntExtra("position", -1);
        Movie movie = ListActivity.movies[position];

        if (position == -1) {
            Picasso.with(DetailsActivity.this)
                    .load(R.raw.load_error)
                    .noFade()
                    .resize(500, 0)
                    .into(mPoster);
            return;
        }

        mTitleTextView.setText(movie.title);
        mReleaseDateTextView.setText(movie.formatedReleaseDate());
        mVoteAverageTextView.setText(movie.formatedVoteAverage());
        mOverviewTextView.setText(movie.overview);

        Picasso.with(DetailsActivity.this)
                .load(NetworkUtils.buildPosterUrl(movie))
                .error(R.raw.load_error)
                .into(mPoster);

        Picasso.with(DetailsActivity.this)
                .load(NetworkUtils.buildBackdropUrl(movie))
                .error(R.raw.load_error)
                .centerInside()
                .fit()
                .into(mBackdrop);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTrailersAdapter = new TrailersAdapter(this);
        mRecyclerView.setAdapter(mTrailersAdapter);

        new FetchTrailersTask().execute(NetworkUtils.buildTrailersUrl(movie.id));

//        for each trailer:
//        Picasso.with(DetailsActivity.this)
//                .load(R.raw.playbutton)
//                .noFade()
//                .resize(100, 100)
//                .error(R.raw.load_error)
//                .into(mPlayButton);
    }

    /**
     * This method is overridden by our DetailsActivity class in order to handle RecyclerView item
     * clicks. It will open youtube or a browser pointed at youtube to play the trailer selected
     *
     * @param trailerKey The key of the trailer that was clicked
     */
    @Override
    public void onClick(String trailerKey) {
        Context context = this;
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailerKey)));
    }

    public class FetchTrailersTask extends AsyncTask<URL, Void, Trailer[]> {
        private final String TAG = DetailsActivity.FetchTrailersTask.class.getSimpleName();

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Trailer[] doInBackground(URL... params) {
            if (isOnline()) {
                URL requestUrl = params[0];

                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                    return JsonUtils.getTrailersArrayFromJson(DetailsActivity.this, jsonResponse);
                } catch (Exception e) {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Trailer[] fetchedTrailers) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (fetchedTrailers != null) {
                mTrailers = fetchedTrailers;
                mTrailersAdapter.setData(mTrailers);
                //loadGridView();
                showTrailers();
            }
            else {
                Log.e(TAG, "No internet access/connection.");
                Toast.makeText(
                        DetailsActivity.this, "No internet connection",
                        Toast.LENGTH_SHORT)
                        .show();
                if (mTrailers == null) {
                    //showErrorMessage();
                }
            }
        }
    }

    private void showTrailers() {
        //mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        //mGridView.setVisibility(View.VISIBLE);
    }
}
