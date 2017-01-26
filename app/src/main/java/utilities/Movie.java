package utilities;

/**
 * Created by piero on 1/24/17.
 */
import java.util.Date;


public class Movie {
    private static final String TAG = Movie.class.getSimpleName();
    public String posterPath, backdropPath, title, overview;
    public Date releaseDate;

    @Override
    public String toString(){
        return title + ", " + releaseDate.toString() + ", " + overview.length() + ", " +
                posterPath.length() + ", " + backdropPath.length();
    }
}
