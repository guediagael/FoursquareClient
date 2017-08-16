package mircod.com.foursquareclient.mvp.models.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by guedi on 8/16/2017.
 */

public interface FoursquareApi {

    @GET("{id}")
    Call<ResponseBody> getVenueDetails (@Path("id")String id, @Query("intent") String intent,
                                        @Query("client_id") String idKey,
                                        @Query("client_secret") String secretKey,
                                        @Query("v") String version,
                                        @Query("m") String m);

    @GET("search")
    Call<ResponseBody> getVenuesList (@Query("ll") String latLong,@Query("intent") String intent,
                                      @Query("client_id") String idKey,
                                      @Query("client_secret") String secretKey,
                                      @Query("v") String version,
                                      @Query("m") String m);
}
