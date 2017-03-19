package com.example.piero.themoviesproject;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piero.themoviesproject.data.MovieContract;
import com.example.piero.themoviesproject.data.MovieContract.MovieEntry;
import com.example.piero.themoviesproject.utilities.JsonUtils;
import com.example.piero.themoviesproject.utilities.Review;
import com.example.piero.themoviesproject.utilities.Trailer;
import com.squareup.picasso.Picasso;

import com.example.piero.themoviesproject.utilities.Movie;
import com.example.piero.themoviesproject.utilities.NetworkUtils;

import java.net.URL;


public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 0;
    private static Trailer[] mTrailers;
    private static Review[] mReviews;
    private ImageView mPoster, mBackdrop, mFavoriteStar;
    private TextView mTitleTextView, mReleaseDateTextView, mVoteAverageTextView, mOverviewTextView;
    private RecyclerView mTrailersRecyclerView, mReviewsRecyclerView;
    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private boolean mIsFavorited;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPoster = (ImageView) findViewById(R.id.iv_poster);
        mBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        mFavoriteStar = (ImageView) findViewById(R.id.iv_favorite);

        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);

        mIsFavorited = false;

        int position = getIntent().getIntExtra("position", -1);
        movie = ListActivity.movies[position];
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

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

        Picasso.with(DetailsActivity.this)
                .load(R.raw.unfavorite_star)
                .noFade()
                .resize(200, 200)
                .error(R.raw.load_error)
                .into(mFavoriteStar);

        mFavoriteStar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mIsFavorited) {
                    mIsFavorited = false;
                    Picasso.with(DetailsActivity.this)
                            .load(R.raw.unfavorite_star)
                            .noFade()
                            .resize(200, 200)
                            .error(R.raw.load_error)
                            .into(mFavoriteStar);
                    Uri uri = MovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(movie.id).build();
                    getContentResolver().delete(uri, null, null);
                }
                else {
                    mIsFavorited = true;
                    Picasso.with(DetailsActivity.this)
                            .load(R.raw.favorite_star)
                            .noFade()
                            .resize(200, 200)
                            .error(R.raw.load_error)
                            .into(mFavoriteStar);
                    // call query to insert this movie's title and id to the db
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieEntry.COLUMN_TITLE, movie.title);
                    contentValues.put(MovieEntry.COLUMN_THEMOVIEDB_ID, movie.id);
                    Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);
                }
            }
        });

        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        LinearLayoutManager trailersLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailersRecyclerView.setLayoutManager(trailersLayoutManager);
        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailersAdapter = new TrailersAdapter(this);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);

        LinearLayoutManager reviewsLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        new FetchTrailersTask().execute(NetworkUtils.buildTrailersUrl(movie.id));
        new FetchReviewsTask().execute(NetworkUtils.buildReviewsUrl(movie.id));
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

    public class FetchReviewsTask extends AsyncTask<URL, Void, Review[]> {
        private final String TAG = DetailsActivity.FetchReviewsTask.class.getSimpleName();

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
        protected Review[] doInBackground(URL... params) {
            if (isOnline()) {
                URL requestUrl = params[0];
                Log.v(TAG, requestUrl.toString());

                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                    return JsonUtils.getReviewsArrayFromJson(DetailsActivity.this, jsonResponse);
                } catch (Exception e) {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Review[] fetchedReviews) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (fetchedReviews != null) {
                mReviews = fetchedReviews;
                mReviewsAdapter.setData(mReviews);
                //loadGridView();
                showReviews();
            }
            else {
                Log.e(TAG, "No internet access/connection.");
                Toast.makeText(
                        DetailsActivity.this, "No internet connection",
                        Toast.LENGTH_SHORT)
                        .show();
                if (mReviews == null) {
                    //showErrorMessage();
                }
            }
        }
    }

    private void showReviews() {
        //mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        //mGridView.setVisibility(View.VISIBLE);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieEntry.CONTENT_URI,
                            null,
                            MovieEntry.COLUMN_THEMOVIEDB_ID + " = '" + movie.id + "'",
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            mIsFavorited = true;
            Picasso.with(DetailsActivity.this)
                    .load(R.raw.favorite_star)
                    .noFade()
                    .resize(200, 200)
                    .error(R.raw.load_error)
                    .into(mFavoriteStar);
        }
        else {
            mIsFavorited = false;
            Picasso.with(DetailsActivity.this)
                    .load(R.raw.unfavorite_star)
                    .noFade()
                    .resize(200, 200)
                    .error(R.raw.load_error)
                    .into(mFavoriteStar);
        }
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
