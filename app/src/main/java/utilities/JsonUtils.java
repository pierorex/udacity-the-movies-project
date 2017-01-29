/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility functions to parse JSON data.
 */
public final class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns an array of Strings
     *
     * @param context Android context (Example: an Activity)
     * @param moviesJsonStr JSON response from server
     *
     * @return Array of Movie instances
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Movie[] getMoviesArrayFromJson(Context context, String moviesJsonStr)
            throws JSONException {
        Movie[] parsedMovieData = null;
        JSONObject resultsJson = new JSONObject(moviesJsonStr);

        /* Is there an error? */
        if (resultsJson.has("code")) {
            int errorCode = resultsJson.getInt("code");

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* probably invalid url */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieArray = resultsJson.getJSONArray("results");
        parsedMovieData = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);
            parsedMovieData[i] = new Movie();
            parsedMovieData[i].title = movieObject.getString("title");
            parsedMovieData[i].releaseDate = dateToString(movieObject.getString("release_date"));
            parsedMovieData[i].posterPath = movieObject.getString("poster_path");
            parsedMovieData[i].backdropPath = movieObject.getString("backdrop_path");
            parsedMovieData[i].overview = movieObject.getString("overview");
            parsedMovieData[i].vote_average =
                    Double.toString(movieObject.getDouble("vote_average"));
        }

        return parsedMovieData;
    }

    public static Date dateToString(String dateString) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = parser.parse(dateString);
        } catch (ParseException e){
            Log.e(TAG, e.getMessage());
        }
        return date;
    }
}