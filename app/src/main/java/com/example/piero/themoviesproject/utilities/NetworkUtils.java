package com.example.piero.themoviesproject.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String IMG_API_URL = "http://image.tmdb.org/t/p/w780/";
    private static final String MOVIES_API_KEY = "ca68ac1bdd44075ddd4efbf40ad75560";
    private static final String MOVIES_API_URL = "https://api.themoviedb.org/3/movie";
    private static final String POPULAR_ENDPOINT = "popular";
    private static final String TOP_RATED_ENDPOINT = "top_rated";
    private static final String TRAILERS_ENDPOINT = "videos";
    private static final String REVIEWS_ENDPOINT = "reviews";


    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param endpoint the endpoint to use for the queries, it might me "popular" or "top_rated"
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String endpoint, String movieId) {
        Uri.Builder uriBuilder = Uri.parse(MOVIES_API_URL).buildUpon();
        if (movieId != null) {
            uriBuilder.appendPath(movieId);
        }
        uriBuilder.appendPath(endpoint)
                .appendQueryParameter("api_key", MOVIES_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildPopularUrl() { return buildUrl(POPULAR_ENDPOINT, null); }

    public static URL buildTopRatedUrl() { return buildUrl(TOP_RATED_ENDPOINT, null); }

    public static URL buildTrailersUrl(String movieId) { return buildUrl(TRAILERS_ENDPOINT, movieId); }

    public static URL buildReviewsUrl(String movieId) { return buildUrl(REVIEWS_ENDPOINT, movieId); }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String buildPosterUrl(Movie movie) {
        return IMG_API_URL + movie.posterPath;
    }

    public static String buildBackdropUrl(Movie movie) {
        return IMG_API_URL + movie.backdropPath;
    }

}
