package com.example.piero.themoviesproject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;

import com.example.piero.themoviesproject.utilities.JsonUtils;
import com.example.piero.themoviesproject.utilities.Movie;
import com.example.piero.themoviesproject.utilities.NetworkUtils;


public class ListActivity extends AppCompatActivity {
    private static final String TAG = ListActivity.class.getSimpleName();
    public static Movie[] movies;
    private String mSortOrder;
    private ProgressBar mLoadingIndicator;
    private GridView mGridView;
    private TextView mErrorMessageDisplay;
    private static final String SORT_ORDER_STATE_KEY = "sort-state-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mGridView = (GridView) findViewById(R.id.gv_movie_list);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mSortOrder = getString(R.string.action_sort_popular);

        // restore previous state
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_ORDER_STATE_KEY)) {
                 mSortOrder = savedInstanceState.getString(SORT_ORDER_STATE_KEY);
            }
        }

        if (mSortOrder.equals(getString(R.string.action_sort_popular)))
            loadMoviesData(NetworkUtils.buildPopularUrl());
        else if (mSortOrder.equals(getString(R.string.action_sort_top_rated)))
            loadMoviesData(NetworkUtils.buildTopRatedUrl());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORT_ORDER_STATE_KEY, mSortOrder);
    }

    private void loadMoviesData(URL url) {
        new FetchMoviesTask().execute(url);
    }

    private void loadGridView() {
        mGridView.setAdapter(new ImageAdapter(this));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    public class FetchMoviesTask extends AsyncTask<URL, Void, Movie[]> {
        private final String TAG = FetchMoviesTask.class.getSimpleName();

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(URL... params) {
            if (isOnline()) {
                URL requestUrl = params[0];

                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                    return JsonUtils.getMoviesArrayFromJson(ListActivity.this, jsonResponse);
                } catch (Exception e) {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] fetchedMovies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (fetchedMovies != null) {
                movies = fetchedMovies;
                loadGridView();
                showMovies();
            }
            else {
                Log.e(TAG, "No internet access/connection.");
                Toast.makeText(
                        ListActivity.this, "No internet connection",
                        Toast.LENGTH_SHORT)
                        .show();
                if (movies == null) {
                    showErrorMessage();
                }
            }
        }
    }

    private class ImageAdapter extends BaseAdapter {
        private final String TAG = ImageAdapter.class.getSimpleName();
        private Context mContext;

        public ImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return movies.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(ListActivity.this)
                    .load(NetworkUtils.buildPosterUrl(movies[position]))
                    .placeholder(R.raw.movie_poster_placeholder)
                    .error(R.raw.load_error)
                    .centerInside()
                    .fit()
                    .into(imageView);
            return imageView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular && !mSortOrder.equals(getString(R.string.action_sort_popular))) {
            mSortOrder = getString(R.string.action_sort_popular);
            loadMoviesData(NetworkUtils.buildPopularUrl());
            return true;
        }

        if (id == R.id.action_sort_top_rated && !mSortOrder.equals(getString(R.string.action_sort_top_rated))) {
            mSortOrder = getString(R.string.action_sort_top_rated);
            loadMoviesData(NetworkUtils.buildTopRatedUrl());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will make the error message visible and hide the movies grid
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        mSortOrder = "error";
        mGridView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovies() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }
}
