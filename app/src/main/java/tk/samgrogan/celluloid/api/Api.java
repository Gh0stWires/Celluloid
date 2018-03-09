package tk.samgrogan.celluloid.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ghost on 3/8/2018.
 */

public interface Api {

    @GET("search/movie?")
    Call<PageResult> search(@Query("api_key") String apiKey, @Query("query") String query);
}
