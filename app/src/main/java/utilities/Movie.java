package utilities;

/**
 * Created by piero on 1/24/17.
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Movie {
    private static final String TAG = Movie.class.getSimpleName();
    public String posterPath, backdropPath, title, overview;
    public Date releaseDate;
    public String vote_average;

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
        return vote_average + " / 10";
    }
}
