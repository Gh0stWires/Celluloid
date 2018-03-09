package tk.samgrogan.celluloid.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ghost on 3/8/2018.
 */

public class Movie {

    @SerializedName("movies")
    @Expose
    private List<MovieResult> movies = null;

    public List<MovieResult> getMovies(){
        return movies;
    }
}
