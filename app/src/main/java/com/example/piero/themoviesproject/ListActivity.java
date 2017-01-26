package com.example.piero.themoviesproject;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.net.URL;

import utilities.JsonUtils;
import utilities.Movie;
import utilities.NetworkUtils;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = ListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        loadPopularMoviesData();
    }

    private void loadPopularMoviesData() {
        //showWeatherDataView();

        new FetchMoviesTask().execute();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            //mLoadingIndicator.setVisibility(View.VISIBLE);
//        }

        @Override
        protected Movie[] doInBackground(Void... params) {
            URL requestUrl = NetworkUtils.buildPopularUrl();

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                return JsonUtils.getMoviesArrayFromJson(ListActivity.this, jsonResponse);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                //showWeatherDataView();
                //mForecastAdapter.setWeatherData(weatherData);
                Log.v(TAG, movies[0].toString());
            }
            //else {
                //showErrorMessage();
            //}
        }
    }
}
