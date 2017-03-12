package com.example.piero.themoviesproject.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Movie {
    private static final String TAG = Movie.class.getSimpleName();
    public String id, posterPath, backdropPath, title, overview;
    public Date releaseDate;
    public String voteAverage;
    public Trailer[] trailers;

    public Movie(String id, String title, Date releaseDate, String posterPath, String backdropPath, String overview, String voteAverage) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    @Override
    public String toString(){
        return title + ", " + releaseDate.toString() + ", " + overview.length() + ", " +
                posterPath.length() + ", " + backdropPath.length();
    }

    public String formatedReleaseDate() {
        DateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        return df.format(releaseDate);
    }

    public String formatedVoteAverage() {
        return voteAverage + " / 10";
    }
}
