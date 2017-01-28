package utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by piero on 1/23/17.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String IMG_API_URL = "http://image.tmdb.org/t/p/w780/";
    private static final String MOVIES_API_KEY = "INSERT_API";
    private static final String MOVIES_API_URL = "https://api.themoviedb.org/3/movie";
    private static final String POPULAR_ENDPOINT = "popular";
    private static final String TOP_RATED_ENDPOINT = "top_rated";


    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param locationQuery The location that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String endpoint) {
        Uri builtUri = Uri.parse(MOVIES_API_URL).buildUpon()
                .appendPath(endpoint)
                .appendQueryParameter("api_key", MOVIES_API_KEY)
                    .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildPopularUrl() {
        return buildUrl(POPULAR_ENDPOINT);
    }

    public static URL buildTopRatedUrl() {
        return buildUrl(TOP_RATED_ENDPOINT);
    }

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
