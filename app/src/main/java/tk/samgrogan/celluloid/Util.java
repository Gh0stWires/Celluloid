package tk.samgrogan.celluloid;

import tk.samgrogan.celluloid.api.Api;
import tk.samgrogan.celluloid.api.TmbdApiClient;

/**
 * Created by ghost on 3/8/2018.
 */

public class Util {

    private Util(){}

    private static final String BASE_URL = "http://api.themoviedb.org/3/";

    public static Api getMovieService(){
        return TmbdApiClient.getClient(BASE_URL).create(Api.class);
    }
}
