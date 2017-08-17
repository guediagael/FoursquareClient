package mircod.com.foursquareclient.mvp.models.apis;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by guedi on 8/16/2017.
 */

public class RetrofitClient {
    private static final String BASE_URL = "https://api.foursquare.com/v2/venues/";
    private static final Retrofit ourInstance = new Retrofit.Builder()
            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
//                    .excludeFieldsWithoutExposeAnnotation().create()))
//            .addConverterFactory(ScalarsConverterFactory.create())
            .build();


    public static Retrofit getInstance() {
        return ourInstance;
    }


}
