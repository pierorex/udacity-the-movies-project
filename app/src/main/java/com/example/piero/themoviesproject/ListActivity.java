package com.example.piero.themoviesproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import utilities.JsonUtils;
import utilities.Movie;
import utilities.NetworkUtils;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = ListActivity.class.getSimpleName();
    public static Movie[] movies;
    String sort_order = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        loadMoviesData(NetworkUtils.buildPopularUrl());
    }

    private void loadMoviesData(URL url) {
        //showWeatherDataView();

        new FetchMoviesTask().execute(url);
    }

    private void loadGridView() {
        GridView gridview = (GridView) findViewById(R.id.gv_movie_list);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            //mLoadingIndicator.setVisibility(View.VISIBLE);
//        }

        @Override
        protected Movie[] doInBackground(URL... params) {
            URL requestUrl = params[0];

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                return JsonUtils.getMoviesArrayFromJson(ListActivity.this, jsonResponse);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] fetchedMovies) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (fetchedMovies != null) {
                //showWeatherDataView();
                //mForecastAdapter.setWeatherData(weatherData);
                movies = fetchedMovies;
                loadGridView();
            }
            //else {
                //showErrorMessage();
            //}
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
            //Log.v(TAG, "getView with position = " + Integer.toString(position));
            ImageView imageView;
            // check to see if we have a view
            if (convertView == null) {
                // no view - so create a new one
                imageView = new ImageView(mContext);
            } else {
                // use the recycled view object
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
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular && sort_order.equals("top_rated")) {
            movies = null;
            sort_order = "popular";
            loadMoviesData(NetworkUtils.buildPopularUrl());
            return true;
        }

        if (id == R.id.action_sort_top_rated && sort_order.equals("popular")) {
            movies = null;
            sort_order = "top_rated";
            loadMoviesData(NetworkUtils.buildTopRatedUrl());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
